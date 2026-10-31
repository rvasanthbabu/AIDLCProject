package com.ideationportal.idea.domain;

import com.ideationportal.core.error.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

/**
 * An employee's idea. Owns the DRAFT -> SUBMITTED transition and draft deletion (U2);
 * later transitions are invoked by U3/U4 through the service boundary and validate the
 * legal source state (BR-U2-10).
 */
@Entity
@Table(name = "ideas")
public class Idea {

    public static final int TITLE_MAX = 150;
    public static final int LONG_TEXT_MAX = 4000;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "submitter_id", nullable = false)
    private UUID submitterId;

    @Column(name = "cycle_id")
    private UUID cycleId;

    @Column(length = TITLE_MAX)
    private String title;

    @Column(name = "problem_statement", length = LONG_TEXT_MAX)
    private String problemStatement;

    @Column(name = "proposed_solution", length = LONG_TEXT_MAX)
    private String proposedSolution;

    @Column(name = "expected_impact", length = LONG_TEXT_MAX)
    private String expectedImpact;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private IdeaCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IdeaState state = IdeaState.DRAFT;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Version
    private long version;

    protected Idea() {
    }

    public Idea(UUID submitterId) {
        this.submitterId = submitterId;
    }

    public void applyDraftFields(String title, String problem, String solution,
                                 String impact, IdeaCategory category) {
        if (state != IdeaState.DRAFT) {
            throw DomainException.conflict("Only a draft idea can be edited.");
        }
        enforceLengths(title, problem, solution, impact);
        this.title = title;
        this.problemStatement = problem;
        this.proposedSolution = solution;
        this.expectedImpact = impact;
        this.category = category;
        touch();
    }

    public void submit(UUID cycleId, Instant when) {
        if (state != IdeaState.DRAFT) {
            throw DomainException.conflict("Only a draft idea can be submitted.");
        }
        requireComplete();
        this.cycleId = cycleId;
        this.state = IdeaState.SUBMITTED;
        this.submittedAt = when;
        touch();
    }

    public void markUnderEvaluation() {
        transition(IdeaState.SUBMITTED, IdeaState.UNDER_EVALUATION);
    }

    public void markScored() {
        transition(IdeaState.UNDER_EVALUATION, IdeaState.SCORED);
    }

    public void markRecognized() {
        transition(IdeaState.SCORED, IdeaState.RECOGNIZED);
    }

    public void markRejected() {
        transition(IdeaState.SCORED, IdeaState.REJECTED);
    }

    public boolean isDraft() {
        return state == IdeaState.DRAFT;
    }

    private void transition(IdeaState from, IdeaState to) {
        if (state != from) {
            throw DomainException.conflict("Illegal state transition from " + state + " to " + to + ".");
        }
        this.state = to;
        touch();
    }

    private void requireComplete() {
        if (isBlank(title) || isBlank(problemStatement) || isBlank(proposedSolution)
                || isBlank(expectedImpact) || category == null) {
            throw DomainException.validation("All fields are required before submission.");
        }
        enforceLengths(title, problemStatement, proposedSolution, expectedImpact);
    }

    private void enforceLengths(String title, String problem, String solution, String impact) {
        if (title != null && title.length() > TITLE_MAX) {
            throw DomainException.validation("Title exceeds maximum length.");
        }
        for (String field : new String[]{problem, solution, impact}) {
            if (field != null && field.length() > LONG_TEXT_MAX) {
                throw DomainException.validation("A text field exceeds maximum length.");
            }
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getSubmitterId() {
        return submitterId;
    }

    public UUID getCycleId() {
        return cycleId;
    }

    public String getTitle() {
        return title;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public String getProposedSolution() {
        return proposedSolution;
    }

    public String getExpectedImpact() {
        return expectedImpact;
    }

    public IdeaCategory getCategory() {
        return category;
    }

    public IdeaState getState() {
        return state;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }
}
