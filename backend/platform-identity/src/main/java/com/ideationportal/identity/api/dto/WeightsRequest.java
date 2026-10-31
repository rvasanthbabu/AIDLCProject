package com.ideationportal.identity.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Criteria weights payload. Range validated here; sum==100 enforced in the service (BR-U1-20).
 */
public record WeightsRequest(
        @Min(0) @Max(100) int feasibility,
        @Min(0) @Max(100) int impact,
        @Min(0) @Max(100) int innovation) {
}
