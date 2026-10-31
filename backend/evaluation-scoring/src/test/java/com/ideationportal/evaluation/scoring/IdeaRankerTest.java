package com.ideationportal.evaluation.scoring;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example-based tests for deterministic ranking and tie-breaking (BR-U3-25).
 */
class IdeaRankerTest {

    @Test
    void ordersByScoreDescending() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        Instant t = Instant.now();
        List<RankedIdea> ranked = IdeaRanker.rank(List.of(
                new IdeaRanker.Scored(a, BigDecimal.valueOf(50), t),
                new IdeaRanker.Scored(b, BigDecimal.valueOf(80), t)));
        assertThat(ranked.get(0).ideaId()).isEqualTo(b);
        assertThat(ranked.get(1).ideaId()).isEqualTo(a);
    }

    @Test
    void tieBrokenByEarliestSubmission() {
        UUID earlier = UUID.randomUUID();
        UUID later = UUID.randomUUID();
        Instant base = Instant.parse("2026-01-01T00:00:00Z");
        List<RankedIdea> ranked = IdeaRanker.rank(List.of(
                new IdeaRanker.Scored(later, BigDecimal.valueOf(75), base.plusSeconds(60)),
                new IdeaRanker.Scored(earlier, BigDecimal.valueOf(75), base)));
        assertThat(ranked.get(0).ideaId()).isEqualTo(earlier);
    }

    @Test
    void finalTieBrokenByIdeaId() {
        Instant t = Instant.parse("2026-01-01T00:00:00Z");
        UUID low = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID high = UUID.fromString("00000000-0000-0000-0000-000000000002");
        List<RankedIdea> ranked = IdeaRanker.rank(List.of(
                new IdeaRanker.Scored(high, BigDecimal.valueOf(60), t),
                new IdeaRanker.Scored(low, BigDecimal.valueOf(60), t)));
        assertThat(ranked.get(0).ideaId()).isEqualTo(low);
    }

    @Test
    void topNReturnsAtMostN() {
        Instant t = Instant.now();
        List<RankedIdea> top = IdeaRanker.topN(List.of(
                new IdeaRanker.Scored(UUID.randomUUID(), BigDecimal.valueOf(10), t),
                new IdeaRanker.Scored(UUID.randomUUID(), BigDecimal.valueOf(20), t),
                new IdeaRanker.Scored(UUID.randomUUID(), BigDecimal.valueOf(30), t),
                new IdeaRanker.Scored(UUID.randomUUID(), BigDecimal.valueOf(40), t)), 3);
        assertThat(top).hasSize(3);
        assertThat(top.get(0).rank()).isEqualTo(1);
    }
}
