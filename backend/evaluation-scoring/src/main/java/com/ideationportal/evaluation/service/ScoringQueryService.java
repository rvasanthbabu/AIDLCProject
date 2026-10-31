package com.ideationportal.evaluation.service;

import com.ideationportal.evaluation.repository.EvaluationRepository;
import com.ideationportal.evaluation.scoring.AggregateResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Read-side aggregation for one idea (on-read, SQL-backed; U3-NFR-PERF-1).
 * Cycle-wide ranking is provided to U4 via {@link #cycleAggregatesProvider()} style access;
 * here we expose per-idea aggregation and a raw cycle aggregate accessor.
 */
@Service
public class ScoringQueryService {

    private final EvaluationRepository evaluations;

    public ScoringQueryService(EvaluationRepository evaluations) {
        this.evaluations = evaluations;
    }

    @Transactional(readOnly = true)
    public AggregateResult aggregateForIdea(UUID ideaId) {
        Object[] row = evaluations.aggregateForIdeaRaw(ideaId);
        // row = [avgNormalized, feasibilitySum, impactSum, innovationSum, count]
        Object[] data = unwrap(row);
        long count = data[4] == null ? 0L : ((Number) data[4]).longValue();
        if (count == 0) {
            return new AggregateResult(ideaId, null, 0, 0, 0, 0);
        }
        BigDecimal avg = data[0] == null ? null : toBigDecimal(data[0]);
        int fSum = ((Number) data[1]).intValue();
        int iSum = ((Number) data[2]).intValue();
        int nSum = ((Number) data[3]).intValue();
        return new AggregateResult(ideaId, avg, fSum, iSum, nSum, (int) count);
    }

    @Transactional(readOnly = true)
    public EvaluationRepository repository() {
        // Exposed for U4 ranking which needs cycle-wide aggregates joined with idea submission times.
        return evaluations;
    }

    private static Object[] unwrap(Object[] row) {
        // Some JPA providers wrap a single-row aggregate result in a nested array.
        if (row.length == 1 && row[0] instanceof Object[] inner) {
            return inner;
        }
        return row;
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        return new BigDecimal(value.toString());
    }
}
