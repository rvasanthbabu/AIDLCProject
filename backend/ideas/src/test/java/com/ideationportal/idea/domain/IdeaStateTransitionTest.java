package com.ideationportal.idea.domain;

import com.ideationportal.core.error.DomainException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Example-based tests for the Idea state machine (BR-U2-05/06/07/10).
 */
class IdeaStateTransitionTest {

    private Idea completeDraft() {
        Idea idea = new Idea(UUID.randomUUID());
        idea.applyDraftFields("Title", "Problem", "Solution", "Impact", IdeaCategory.PROCESS);
        return idea;
    }

    @Test
    void submitRequiresAllFields() {
        Idea idea = new Idea(UUID.randomUUID());
        idea.applyDraftFields("Title", null, null, null, IdeaCategory.PROCESS);
        assertThatThrownBy(() -> idea.submit(UUID.randomUUID(), Instant.now()))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void draftSubmitsToSubmitted() {
        Idea idea = completeDraft();
        idea.submit(UUID.randomUUID(), Instant.now());
        assertThat(idea.getState()).isEqualTo(IdeaState.SUBMITTED);
        assertThat(idea.getSubmittedAt()).isNotNull();
    }

    @Test
    void cannotEditAfterSubmit() {
        Idea idea = completeDraft();
        idea.submit(UUID.randomUUID(), Instant.now());
        assertThatThrownBy(() -> idea.applyDraftFields("x", "y", "z", "w", IdeaCategory.OTHER))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void fullLifecycleTransitions() {
        Idea idea = completeDraft();
        idea.submit(UUID.randomUUID(), Instant.now());
        idea.markUnderEvaluation();
        idea.markScored();
        idea.markRecognized();
        assertThat(idea.getState()).isEqualTo(IdeaState.RECOGNIZED);
    }

    @Test
    void illegalTransitionRejected() {
        Idea idea = completeDraft();
        // Cannot go straight to SCORED from DRAFT.
        assertThatThrownBy(idea::markScored).isInstanceOf(DomainException.class);
    }
}
