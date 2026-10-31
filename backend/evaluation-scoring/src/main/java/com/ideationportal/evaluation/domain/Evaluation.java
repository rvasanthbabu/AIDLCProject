package com.ideationportal.evaluation.domain;

import com.ideationportal.evaluation.scoring.ScoreCalculator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * A panel member's evaluation of an idea (Stories 2.2/2.4). One per (idea, member).
 * normalizedScore is recomputed from current global weights whenever scores change.
 */
@Entity
@Table(name = "evaluations",
        uniqueConstraints = @UniqueConstraint(name = "ux_evaluation_idea_member",
                columnNames = {"idea_id", "panel_member_id"}))
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "idea_id", nullable = false)
    private UUID ideaId;

    @Column(name = "panel_member_id", nullable = false)
    private UUID panelMemberId;

    @Column(name = "cycle_id", nullable = false)
    private UUID cycleId;

    @Column(nullable = false)
    private int feasibility;

    @Column(nullable = false)
    private int impact;

    @Column(nullable = false)
    private int innovation;

    @Column(name = "normalized_score", nullable = false, precision = 12, scale = 4)
    private BigDecimal normalizedScore;

    @Column(length = 2000)
    private String comment;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Version
    private long version;

    protected Evaluation() {
    }

    public Evaluation(UUID ideaId, UUID panelMemberId, UUID cycleId,
                      int feasibility, int impact, int innovation, String comment,
                      int wF, int wI, int wN) {
        this.ideaId = ideaId;
        this.panelMemberId = panelMemberId;
        this.cycleId = cycleId;
        applyScores(feasibility, impact, innovation, comment, wF, wI, wN);
    }

    public void applyScores(int feasibility, int impact, int innovation, String comment,
                            int wF, int wI, int wN) {
        // ScoreCalculator validates metric ranges and weights (fails closed).
        this.normalizedScore = ScoreCalculator.normalizedScore(feasibility, impact, innovation, wF, wI, wN);
        this.feasibility = feasibility;
        this.impact = impact;
        this.innovation = innovation;
        this.comment = comment;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getIdeaId() {
        return ideaId;
    }

    public UUID getPanelMemberId() {
        return panelMemberId;
    }

    public UUID getCycleId() {
        return cycleId;
    }

    public int getFeasibility() {
        return feasibility;
    }

    public int getImpact() {
        return impact;
    }

    public int getInnovation() {
        return innovation;
    }

    public BigDecimal getNormalizedScore() {
        return normalizedScore;
    }

    public String getComment() {
        return comment;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }
}
