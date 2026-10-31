package com.ideationportal.idea.api.dto;

import com.ideationportal.idea.domain.Idea;
import com.ideationportal.idea.domain.IdeaCategory;
import com.ideationportal.idea.domain.IdeaState;

import java.time.Instant;
import java.util.UUID;

/**
 * Full idea view returned to the owner. Pure data carrier; supports serialization round-trip PBT.
 */
public record IdeaResponse(
        UUID id,
        UUID submitterId,
        UUID cycleId,
        String title,
        String problemStatement,
        String proposedSolution,
        String expectedImpact,
        IdeaCategory category,
        IdeaState state,
        Instant submittedAt) {

    public static IdeaResponse from(Idea idea) {
        return new IdeaResponse(
                idea.getId(),
                idea.getSubmitterId(),
                idea.getCycleId(),
                idea.getTitle(),
                idea.getProblemStatement(),
                idea.getProposedSolution(),
                idea.getExpectedImpact(),
                idea.getCategory(),
                idea.getState(),
                idea.getSubmittedAt());
    }
}
