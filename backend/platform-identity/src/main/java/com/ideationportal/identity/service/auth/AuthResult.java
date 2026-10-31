package com.ideationportal.identity.service.auth;

import com.ideationportal.identity.domain.Role;

import java.util.UUID;

/**
 * Result of an authentication attempt. When {@code mfaRequired} is true, no session is
 * established yet; the caller must complete MFA verification first.
 */
public record AuthResult(boolean authenticated,
                         boolean mfaRequired,
                         UUID userId,
                         Role role,
                         String displayName) {

    public static AuthResult mfaChallenge(UUID userId) {
        return new AuthResult(false, true, userId, null, null);
    }

    public static AuthResult success(UUID userId, Role role, String displayName) {
        return new AuthResult(true, false, userId, role, displayName);
    }
}
