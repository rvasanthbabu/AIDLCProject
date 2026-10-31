package com.ideationportal.idea.service;

import com.ideationportal.idea.domain.IdeaCategory;

/**
 * Service-layer input for draft content (API DTOs map to this).
 */
public record IdeaDraftData(String title,
                            String problemStatement,
                            String proposedSolution,
                            String expectedImpact,
                            IdeaCategory category) {
}
