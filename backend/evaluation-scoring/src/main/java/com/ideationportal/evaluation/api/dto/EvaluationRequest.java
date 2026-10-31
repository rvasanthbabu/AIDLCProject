package com.ideationportal.evaluation.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Panel member's evaluation input. Metrics constrained to 1-5 (SECURITY-05; BR-U3-06).
 * The idea id comes from the path; the cycle is derived from the idea/active cycle server-side.
 */
public record EvaluationRequest(
        @Min(1) @Max(5) int feasibility,
        @Min(1) @Max(5) int impact,
        @Min(1) @Max(5) int innovation,
        @Size(max = 2000) String comment) {
}
