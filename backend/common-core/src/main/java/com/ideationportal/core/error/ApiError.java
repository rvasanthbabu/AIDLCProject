package com.ideationportal.core.error;

import java.time.Instant;

/**
 * Generic, user-safe error payload (SECURITY-09/15): no stack traces or internal details.
 */
public record ApiError(Instant timestamp, int status, String error, String message, String correlationId) {
}
