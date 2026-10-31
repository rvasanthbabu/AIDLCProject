package com.ideationportal.idea.api.dto;

import com.ideationportal.idea.domain.Idea;
import com.ideationportal.idea.domain.IdeaCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Create/update draft payload. Fields are optional at draft time but length-bounded (SECURITY-05).
 * Full completeness is enforced at submission in the domain.
 */
public record IdeaRequest(
        @Size(max = Idea.TITLE_MAX) String title,
        @Size(max = Idea.LONG_TEXT_MAX) String problemStatement,
        @Size(max = Idea.LONG_TEXT_MAX) String proposedSolution,
        @Size(max = Idea.LONG_TEXT_MAX) String expectedImpact,
        @NotNull IdeaCategory category) {
}
