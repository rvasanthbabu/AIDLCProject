package com.ideationportal.idea.api.dto;

import com.ideationportal.idea.domain.Idea;
import com.ideationportal.idea.domain.IdeaCategory;
import com.ideationportal.idea.domain.IdeaState;

import java.util.UUID;

/**
 * Compact idea view for list screens (Story 1.3).
 */
public record IdeaSummaryResponse(UUID id, String title, IdeaCategory category, IdeaState state) {

    public static IdeaSummaryResponse from(Idea idea) {
        return new IdeaSummaryResponse(idea.getId(), idea.getTitle(), idea.getCategory(), idea.getState());
    }
}
