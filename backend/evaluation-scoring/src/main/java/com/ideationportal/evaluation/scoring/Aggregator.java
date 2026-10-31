package com.ideationportal.evaluation.scoring;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 * Pure aggregation (BR-U3-22/23). aggregateScore = mean of normalized scores (Q2=A);
 * per-metric values are SUMs (Q3=B).
 */
public final class Aggregator {

    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    /** One panel member's contribution to aggregation. */
    public record EvaluationInput(int feasibility, int impact, int innovation, BigDecimal normalizedScore) {
    }

    private Aggregator() {
    }

    public static AggregateResult aggregate(java.util.UUID ideaId, List<EvaluationInput> evaluations) {
        if (evaluations == null || evaluations.isEmpty()) {
            return new AggregateResult(ideaId, null, 0, 0, 0, 0);
        }
        BigDecimal sum = BigDecimal.ZERO;
        int fSum = 0, iSum = 0, nSum = 0;
        for (EvaluationInput e : evaluations) {
            sum = sum.add(e.normalizedScore());
            fSum += e.feasibility();
            iSum += e.impact();
            nSum += e.innovation();
        }
        BigDecimal mean = sum.divide(BigDecimal.valueOf(evaluations.size()), MC).stripTrailingZeros();
        return new AggregateResult(ideaId, mean, fSum, iSum, nSum, evaluations.size());
    }
}
