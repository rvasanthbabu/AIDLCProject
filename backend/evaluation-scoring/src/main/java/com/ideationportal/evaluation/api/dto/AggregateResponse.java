package com.ideationportal.evaluation.api.dto;

import com.ideationportal.evaluation.scoring.AggregateResult;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Aggregated scoring view for an idea (dashboard/leaderboard reuse).
 */
public record AggregateResponse(
        UUID ideaId,
        BigDecimal aggregateScore,
        int feasibilitySum,
        int impactSum,
        int innovationSum,
        int evaluatorCount) {

    public static AggregateResponse from(AggregateResult r) {
        return new AggregateResponse(r.ideaId(), r.aggregateScore(),
                r.feasibilitySum(), r.impactSum(), r.innovationSum(), r.evaluatorCount());
    }
}
