package com.ideationportal.evaluation.scoring;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Property-based tests for Aggregator (PBT-03 invariant: mean within [min,max] of inputs).
 */
class AggregatorPropertyTest {

    @Provide
    Arbitrary<List<Aggregator.EvaluationInput>> evaluationLists() {
        Arbitrary<Aggregator.EvaluationInput> one = Arbitraries.integers().between(1, 5)
                .flatMap(f -> Arbitraries.integers().between(1, 5)
                        .flatMap(i -> Arbitraries.integers().between(1, 5)
                                .map(n -> new Aggregator.EvaluationInput(
                                        f, i, n,
                                        // use an equal-weight normalized score for a well-defined input
                                        ScoreCalculator.normalizedScore(f, i, n, 34, 33, 33)))));
        return one.list().ofMinSize(1).ofMaxSize(20);
    }

    /** Mean aggregate is within [min, max] of the input normalized scores. */
    @Property
    void aggregateWithinInputBounds(@ForAll("evaluationLists") List<Aggregator.EvaluationInput> evals) {
        AggregateResult result = Aggregator.aggregate(UUID.randomUUID(), evals);
        BigDecimal min = evals.get(0).normalizedScore();
        BigDecimal max = evals.get(0).normalizedScore();
        for (Aggregator.EvaluationInput e : evals) {
            if (e.normalizedScore().compareTo(min) < 0) {
                min = e.normalizedScore();
            }
            if (e.normalizedScore().compareTo(max) > 0) {
                max = e.normalizedScore();
            }
        }
        assertThat(result.aggregateScore()).isBetween(min, max);
        assertThat(result.evaluatorCount()).isEqualTo(evals.size());
    }

    /** Single evaluation: aggregate equals that evaluation's score. */
    @Property
    void singleEvaluationEqualsItsScore(@ForAll("evaluationLists") List<Aggregator.EvaluationInput> evals) {
        Aggregator.EvaluationInput single = evals.get(0);
        AggregateResult result = Aggregator.aggregate(UUID.randomUUID(), List.of(single));
        assertThat(result.aggregateScore()).isEqualByComparingTo(single.normalizedScore());
    }

    /** No evaluations: aggregate score is null, count zero. */
    @Property
    void emptyIsNull() {
        AggregateResult result = Aggregator.aggregate(UUID.randomUUID(), List.of());
        assertThat(result.aggregateScore()).isNull();
        assertThat(result.evaluatorCount()).isZero();
    }
}
