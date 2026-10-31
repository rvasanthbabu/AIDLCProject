package com.ideationportal.evaluation.scoring;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Pure, deterministic ranking (BR-U3-25): aggregateScore DESC, then submittedAt ASC
 * (earliest first; FR-6.2), then ideaId ASC (stable secondary; Q4=A).
 */
public final class IdeaRanker {

    /** Input for ranking: an idea with its aggregate and submission time. */
    public record Scored(UUID ideaId, BigDecimal aggregateScore, Instant submittedAt) {
    }

    private static final Comparator<Scored> ORDER = Comparator
            .comparing(Scored::aggregateScore, Comparator.reverseOrder())
            .thenComparing(Scored::submittedAt, Comparator.naturalOrder())
            .thenComparing(Scored::ideaId, Comparator.naturalOrder());

    private IdeaRanker() {
    }

    /**
     * @return ranked ideas (1-based rank), excluding any with a null aggregate score.
     */
    public static List<RankedIdea> rank(List<Scored> scored) {
        List<Scored> eligible = new ArrayList<>();
        for (Scored s : scored) {
            if (s.aggregateScore() != null) {
                eligible.add(s);
            }
        }
        eligible.sort(ORDER);
        List<RankedIdea> result = new ArrayList<>(eligible.size());
        int rank = 1;
        for (Scored s : eligible) {
            result.add(new RankedIdea(s.ideaId(), s.aggregateScore(), s.submittedAt(), rank++));
        }
        return result;
    }

    /**
     * @return the top N ranked ideas.
     */
    public static List<RankedIdea> topN(List<Scored> scored, int n) {
        List<RankedIdea> ranked = rank(scored);
        return ranked.subList(0, Math.min(n, ranked.size()));
    }
}
