package com.ideationportal.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

/**
 * Global evaluation-metric weights (decision Q2=A). The three weights MUST sum to 100
 * (BR-U1-20); validation is enforced in the service via {@code WeightsValidator}.
 * A single active row is maintained (fixed id).
 */
@Entity
@Table(name = "criteria_weights")
public class CriteriaWeights {

    /** Fixed singleton id for the single global weights row. */
    public static final UUID SINGLETON_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Id
    private UUID id = SINGLETON_ID;

    @Column(name = "feasibility_weight", nullable = false)
    private int feasibilityWeight;

    @Column(name = "impact_weight", nullable = false)
    private int impactWeight;

    @Column(name = "innovation_weight", nullable = false)
    private int innovationWeight;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Version
    private long version;

    protected CriteriaWeights() {
    }

    public CriteriaWeights(int feasibilityWeight, int impactWeight, int innovationWeight, UUID updatedBy) {
        apply(feasibilityWeight, impactWeight, innovationWeight, updatedBy);
    }

    public void apply(int feasibility, int impact, int innovation, UUID updatedBy) {
        this.feasibilityWeight = feasibility;
        this.impactWeight = impact;
        this.innovationWeight = innovation;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public int getFeasibilityWeight() {
        return feasibilityWeight;
    }

    public int getImpactWeight() {
        return impactWeight;
    }

    public int getInnovationWeight() {
        return innovationWeight;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
