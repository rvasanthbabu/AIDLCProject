package com.ideationportal.identity.api.dto;

import com.ideationportal.identity.domain.Role;

import java.util.UUID;

/**
 * Auth response. When {@code mfaRequired} is true the client must call the MFA verify endpoint.
 * Never includes password hashes or MFA secrets.
 */
public record AuthResponse(boolean mfaRequired, UUID userId, Role role, String displayName) {
}
