package com.ideationportal.idea.domain;

/**
 * Idea lifecycle (FR-8.1). Legal transitions:
 * DRAFT -> SUBMITTED -> UNDER_EVALUATION -> SCORED -> RECOGNIZED | REJECTED.
 */
public enum IdeaState {
    DRAFT,
    SUBMITTED,
    UNDER_EVALUATION,
    SCORED,
    RECOGNIZED,
    REJECTED
}
