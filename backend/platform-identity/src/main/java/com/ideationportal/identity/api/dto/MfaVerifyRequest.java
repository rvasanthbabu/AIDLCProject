package com.ideationportal.identity.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * MFA verification payload: 6-digit TOTP code.
 */
public record MfaVerifyRequest(
        @NotBlank @Pattern(regexp = "\\d{6}", message = "MFA code must be 6 digits") String code) {
}
