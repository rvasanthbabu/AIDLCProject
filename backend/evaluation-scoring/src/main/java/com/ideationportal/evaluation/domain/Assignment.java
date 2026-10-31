package com.ideationportal.evaluation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

/**
 * Assignment of an idea to a panel member for evaluation (Story 3.3). Removing an assignment
 * sets active=false but does not delete the member's evaluation (BR-U3-11).
 */
@Entity
@Table(name = "assignments",
        uniqueConstraints = @UniqueConstraint(name = "ux_assignment_idea_member",
                columnNames = {"idea_id", "panel_member_id"}))
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "idea_id", nullable = false)
    private UUID ideaId;

    @Column(name = "panel_member_id", nullable = false)
    private UUID panelMemberId;

    @Column(name = "cycle_id", nullable = false)
    private UUID cycleId;

    @Column(name = "assigned_by", nullable = false)
    private UUID assignedBy;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt = Instant.now();

    @Column(nullable = false)
    private boolean active = true;

    protected Assignment() {
    }

    public Assignment(UUID ideaId, UUID panelMemberId, UUID cycleId, UUID assignedBy) {
        this.ideaId = ideaId;
        this.panelMemberId = panelMemberId;
        this.cycleId = cycleId;
        this.assignedBy = assignedBy;
    }

    public void deactivate() {
        this.active = false;
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

    public boolean isActive() {
        return active;
    }
}
