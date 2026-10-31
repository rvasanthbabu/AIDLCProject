package com.ideationportal.evaluation.scoring;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * An idea with its aggregate score and 1-based rank.
 */
public record RankedIdea(UUID ideaId,
                         BigDecimal aggregateScore,
                         Instant submittedAt,
                         int rank) {
}
