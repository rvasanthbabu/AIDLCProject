package com.ideationportal.evaluation.scoring;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Aggregated scoring result for an idea (computed on read). aggregateScore is null when
 * there are no evaluations.
 */
public record AggregateResult(UUID ideaId,
                              BigDecimal aggregateScore,
                              int feasibilitySum,
                              int impactSum,
                              int innovationSum,
                              int evaluatorCount) {
}
