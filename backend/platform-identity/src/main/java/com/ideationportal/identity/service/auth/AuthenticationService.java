package com.ideationportal.identity.service.auth;

import com.ideationportal.core.audit.AuditLogWriter;
import com.ideationportal.core.error.DomainException;
import com.ideationportal.identity.domain.User;
import com.ideationportal.identity.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Login orchestration (Story 3.1): lockout (BR-U1-04), generic failures (BR-U1-05),
 * and MFA gating (BR-U1-07). Fails closed on any error.
 */
@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository users;
    private final LocalPasswordProvider localProvider;
    private final TotpService totpService;
    private final AuditLogWriter audit;
    private final AuthProperties props;

    public AuthenticationService(UserRepository users,
                                 LocalPasswordProvider localProvider,
                                 TotpService totpService,
                                 AuditLogWriter audit,
                                 AuthProperties props) {
        this.users = users;
        this.localProvider = localProvider;
        this.totpService = totpService;
        this.audit = audit;
        this.props = props;
    }

    @Transactional
    public AuthResult login(String email, String password) {
        Instant now = Instant.now();
        Optional<User> maybeUser = users.findByEmailIgnoreCase(email);

        // Lockout check first (do not reveal existence).
        if (maybeUser.isPresent() && maybeUser.get().isLocked(now)) {
            audit.record(maybeUser.get().getId(), "LOGIN_LOCKED", "user:" + email, "Account locked");
            throw DomainException.unauthorized("Account is temporarily locked. Try again later.");
        }

        Optional<User> authenticated = localProvider.authenticate(email, password);
        if (authenticated.isEmpty()) {
            maybeUser.ifPresent(u -> {
                u.registerFailedLogin(props.getLockoutThreshold(), now.plus(props.getLockoutDuration()));
                users.save(u);
                audit.record(u.getId(), "LOGIN_FAILED", "user:" + email, "Invalid credentials");
            });
            // Generic message regardless of whether the email exists (BR-U1-05).
            throw DomainException.unauthorized("Invalid email or password.");
        }

        User user = authenticated.get();
        user.registerSuccessfulLogin();
        users.save(user);

        if (user.requiresMfa()) {
            // Issue MFA challenge; full session is established after verifyMfa (BR-U1-07).
            return AuthResult.mfaChallenge(user.getId());
        }

        audit.record(user.getId(), "LOGIN_SUCCESS", "user:" + email, null);
        return AuthResult.success(user.getId(), user.getRole(), user.getDisplayName());
    }

    @Transactional
    public AuthResult verifyMfa(java.util.UUID userId, String code) {
        User user = users.findById(userId)
                .orElseThrow(() -> DomainException.unauthorized("Invalid MFA session."));
        if (user.getMfaSecret() == null || !totpService.verify(user.getMfaSecret(), code)) {
            audit.record(user.getId(), "MFA_FAILED", "user:" + user.getEmail(), null);
            throw DomainException.unauthorized("Invalid MFA code.");
        }
        audit.record(user.getId(), "LOGIN_SUCCESS_MFA", "user:" + user.getEmail(), null);
        return AuthResult.success(user.getId(), user.getRole(), user.getDisplayName());
    }
}
