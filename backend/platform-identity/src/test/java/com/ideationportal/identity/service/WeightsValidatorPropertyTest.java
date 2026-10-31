package com.ideationportal.identity.service;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Property-based tests for weight validation (PBT-03 invariant). jqwik provides generation,
 * shrinking, and seed-based reproducibility (PBT-07/08).
 */
class WeightsValidatorPropertyTest {

    /** Invariant: a triple is valid iff each weight is in [0,100] and the sum is exactly 100. */
    @Property
    void validIffInRangeAndSums100(@ForAll @IntRange(min = -50, max = 200) int f,
                                   @ForAll @IntRange(min = -50, max = 200) int i,
                                   @ForAll @IntRange(min = -50, max = 200) int n) {
        boolean expected = f >= 0 && f <= 100
                && i >= 0 && i <= 100
                && n >= 0 && n <= 100
                && (f + i + n) == 100;
        assertThat(WeightsValidator.isValid(f, i, n)).isEqualTo(expected);
    }

    /** Any triple summing to 100 within range is accepted. */
    @Property
    void acceptsAllValidPartitions(@ForAll @IntRange(min = 0, max = 100) int f,
                                   @ForAll @IntRange(min = 0, max = 100) int i) {
        int n = 100 - f - i;
        if (n < 0 || n > 100) {
            return; // skip combinations that cannot form a valid partition
        }
        assertThat(WeightsValidator.isValid(f, i, n)).isTrue();
    }
}
