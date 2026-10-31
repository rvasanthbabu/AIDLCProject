package com.ideationportal.evaluation.scoring;

import com.ideationportal.core.error.DomainException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pure scoring functions (BR-U3-20/21). No framework dependencies -> ideal for PBT.
 *
 * normalizedScore = (weightedSum - 100) / 400 * 100, where
 * weightedSum = f*wF + i*wI + n*wN with metrics in [1,5] and weights summing to 100.
 * Range: all-1s -> 0, all-5s -> 100.
 */
public final class ScoreCalculator {

    public static final int METRIC_MIN = 1;
    public static final int METRIC_MAX = 5;
    private static final BigDecimal MIN_WEIGHTED_SUM = BigDecimal.valueOf(100); // all metrics = 1, weights sum 100
    private static final BigDecimal RANGE = BigDecimal.valueOf(400);            // 500 - 100
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    private ScoreCalculator() {
    }

    public static int weightedSum(int feasibility, int impact, int innovation,
                                  int wF, int wI, int wN) {
        requireMetric(feasibility);
        requireMetric(impact);
        requireMetric(innovation);
        requireWeights(wF, wI, wN);
        return feasibility * wF + impact * wI + innovation * wN;
    }

    /**
     * @return normalized score in [0,100] with BigDecimal precision.
     */
    public static BigDecimal normalizedScore(int feasibility, int impact, int innovation,
                                             int wF, int wI, int wN) {
        BigDecimal ws = BigDecimal.valueOf(weightedSum(feasibility, impact, innovation, wF, wI, wN));
        return ws.subtract(MIN_WEIGHTED_SUM)
                .divide(RANGE, MC)
                .multiply(HUNDRED, MC)
                .stripTrailingZeros();
    }

    private static void requireMetric(int m) {
        if (m < METRIC_MIN || m > METRIC_MAX) {
            throw DomainException.validation("Metric scores must be between 1 and 5.");
        }
    }

    private static void requireWeights(int wF, int wI, int wN) {
        if (wF < 0 || wI < 0 || wN < 0 || (wF + wI + wN) != 100) {
            throw DomainException.validation("Weights must be non-negative and sum to 100.");
        }
    }
}
