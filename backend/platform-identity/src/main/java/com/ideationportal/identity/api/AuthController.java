package com.ideationportal.identity.api;

import com.ideationportal.core.error.DomainException;
import com.ideationportal.core.security.RateLimiter;
import com.ideationportal.identity.api.dto.AuthResponse;
import com.ideationportal.identity.api.dto.LoginRequest;
import com.ideationportal.identity.api.dto.MfaVerifyRequest;
import com.ideationportal.identity.service.auth.AuthResult;
import com.ideationportal.identity.service.auth.AuthProperties;
import com.ideationportal.identity.service.auth.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Authentication endpoints (Story 3.1). Login is rate limited (SECURITY-11) and public;
 * MFA verification completes the session for MFA-required users.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String PENDING_MFA_USER = "PENDING_MFA_USER_ID";

    private final AuthenticationService authService;
    private final RateLimiter rateLimiter;
    private final AuthProperties authProperties;

    public AuthController(AuthenticationService authService,
                          RateLimiter rateLimiter,
                          AuthProperties authProperties) {
        this.authService = authService;
        this.rateLimiter = rateLimiter;
        this.authProperties = authProperties;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest http) {
        String rateKey = "login:" + clientKey(http);
        if (!rateLimiter.tryAcquire(rateKey, authProperties.getLoginRateCapacity(), java.time.Duration.ofMinutes(1))) {
            throw new DomainException(DomainException.Kind.CONFLICT, "Too many attempts. Please wait and retry.");
        }
        AuthResult result = authService.login(request.email(), request.password());
        if (result.mfaRequired()) {
            // Stash pending MFA user in session; do not establish full auth yet.
            HttpSession session = http.getSession(true);
            session.setAttribute(PENDING_MFA_USER, result.userId());
            return ResponseEntity.ok(new AuthResponse(true, result.userId(), null, null));
        }
        establishSession(http, new CurrentUser(result.userId(), result.role(), result.displayName()));
        return ResponseEntity.ok(new AuthResponse(false, result.userId(), result.role(), result.displayName()));
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<AuthResponse> verifyMfa(@Valid @RequestBody MfaVerifyRequest request,
                                                  HttpServletRequest http) {
        HttpSession session = http.getSession(false);
        Object pending = session == null ? null : session.getAttribute(PENDING_MFA_USER);
        if (!(pending instanceof UUID userId)) {
            throw DomainException.unauthorized("No pending MFA session.");
        }
        AuthResult result = authService.verifyMfa(userId, request.code());
        session.removeAttribute(PENDING_MFA_USER);
        establishSession(http, new CurrentUser(result.userId(), result.role(), result.displayName()));
        return ResponseEntity.ok(new AuthResponse(false, result.userId(), result.role(), result.displayName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest http) {
        HttpSession session = http.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    private void establishSession(HttpServletRequest http, CurrentUser user) {
        SecurityContextHelper.establish(user);
        HttpSession session = http.getSession(true);
        // Persist the SecurityContext in the session so it is validated on each subsequent request.
        session.setAttribute("SPRING_SECURITY_CONTEXT",
                org.springframework.security.core.context.SecurityContextHolder.getContext());
    }

    private String clientKey(HttpServletRequest http) {
        String forwarded = http.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return http.getRemoteAddr();
    }
}
