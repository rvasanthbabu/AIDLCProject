package com.ideationportal.identity.service;

import com.ideationportal.core.audit.AuditLogWriter;
import com.ideationportal.core.error.DomainException;
import com.ideationportal.identity.domain.Role;
import com.ideationportal.identity.domain.User;
import com.ideationportal.identity.repository.UserRepository;
import com.ideationportal.identity.service.PasswordPolicy.BreachedPasswordChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * User and role management (Stories 7.1). Administrator-only operations; authorization is
 * enforced at the API layer and (defense in depth) by requiring an admin actor here.
 */
@Service
public class UserAccountService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final BreachedPasswordChecker breachedChecker;
    private final AuditLogWriter audit;

    public UserAccountService(UserRepository users,
                              PasswordEncoder passwordEncoder,
                              BreachedPasswordChecker breachedChecker,
                              AuditLogWriter audit) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.breachedChecker = breachedChecker;
        this.audit = audit;
    }

    @Transactional
    public User createUser(UUID actorId, String email, String rawPassword, String displayName, Role role) {
        if (users.existsByEmailIgnoreCase(email)) {
            throw DomainException.conflict("A user with this email already exists.");
        }
        validatePassword(rawPassword);
        User user = new User(email, passwordEncoder.encode(rawPassword), displayName, role);
        User saved = users.save(user);
        audit.record(actorId, "USER_CREATED", "user:" + saved.getId(), "role=" + role);
        return saved;
    }

    /**
     * Read-only role lookup for other units' authorization checks (e.g., U3 verifying a
     * user is a PANEL_MEMBER before assigning an idea).
     */
    @Transactional(readOnly = true)
    public Role getRole(UUID userId) {
        return users.findById(userId)
                .orElseThrow(() -> DomainException.notFound("User not found."))
                .getRole();
    }

    @Transactional
    public User assignRole(UUID actorId, UUID userId, Role newRole) {
        User user = users.findById(userId)
                .orElseThrow(() -> DomainException.notFound("User not found."));
        Role previous = user.getRole();
        user.changeRole(newRole);
        User saved = users.save(user);
        audit.record(actorId, "USER_ROLE_CHANGED", "user:" + userId, previous + "->" + newRole);
        return saved;
    }

    private void validatePassword(String rawPassword) {
        if (!PasswordPolicy.meetsComplexity(rawPassword)) {
            throw DomainException.validation(
                    "Password must be at least 8 characters and include upper, lower, digit, and symbol.");
        }
        if (breachedChecker.isBreached(rawPassword)) {
            throw DomainException.validation("This password has appeared in a known breach. Choose another.");
        }
    }
}
