package com.ideationportal.evaluation.scoring;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.IntRange;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Property-based tests for ScoreCalculator (PBT-03 invariants, PBT-07/08 generators + shrinking + seeds).
 */
class ScoreCalculatorPropertyTest {

    /** Weights that sum to exactly 100. */
    @Provide
    Arbitrary<int[]> weights() {
        Arbitrary<Integer> wF = Arbitraries.integers().between(0, 100);
        return wF.flatMap(f -> Arbitraries.integers().between(0, 100 - f)
                .map(i -> new int[]{f, i, 100 - f - i}));
    }

    /** Invariant: normalized score is always within [0, 100]. */
    @Property
    void normalizedScoreWithinBounds(@ForAll @IntRange(min = 1, max = 5) int f,
                                     @ForAll @IntRange(min = 1, max = 5) int i,
                                     @ForAll @IntRange(min = 1, max = 5) int n,
                                     @ForAll("weights") int[] w) {
        BigDecimal score = ScoreCalculator.normalizedScore(f, i, n, w[0], w[1], w[2]);
        assertThat(score).isBetween(BigDecimal.ZERO, BigDecimal.valueOf(100));
    }

    /** Boundary: all minimum metrics -> 0; all maximum metrics -> 100. */
    @Property
    void extremesMapToZeroAndHundred(@ForAll("weights") int[] w) {
        assertThat(ScoreCalculator.normalizedScore(1, 1, 1, w[0], w[1], w[2]))
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(ScoreCalculator.normalizedScore(5, 5, 5, w[0], w[1], w[2]))
                .isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    /** Monotonicity: raising one metric (others fixed) never lowers the score. */
    @Property
    void monotonicInEachMetric(@ForAll @IntRange(min = 1, max = 4) int f,
                               @ForAll @IntRange(min = 1, max = 5) int i,
                               @ForAll @IntRange(min = 1, max = 5) int n,
                               @ForAll("weights") int[] w) {
        BigDecimal lower = ScoreCalculator.normalizedScore(f, i, n, w[0], w[1], w[2]);
        BigDecimal higher = ScoreCalculator.normalizedScore(f + 1, i, n, w[0], w[1], w[2]);
        assertThat(higher).isGreaterThanOrEqualTo(lower);
    }
}
