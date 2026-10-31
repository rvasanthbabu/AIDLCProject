package com.ideationportal.identity.domain;

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
 * Time-boxed evaluation cycle. State machine DRAFT -> OPEN -> CLOSED (BR-U1-16).
 */
@Entity
@Table(name = "cycles")
public class Cycle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CycleState state = CycleState.DRAFT;

    @Column(name = "open_date", nullable = false)
    private Instant openDate;

    @Column(name = "close_date", nullable = false)
    private Instant closeDate;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Version
    private long version;

    protected Cycle() {
    }

    public Cycle(String name, Instant openDate, Instant closeDate, UUID createdBy) {
        if (!closeDate.isAfter(openDate)) {
            throw DomainException.validation("closeDate must be after openDate.");
        }
        this.name = name;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.createdBy = createdBy;
    }

    public void open() {
        if (state != CycleState.DRAFT) {
            throw DomainException.conflict("Only a DRAFT cycle can be opened.");
        }
        this.state = CycleState.OPEN;
        touch();
    }

    public void close(Instant when) {
        if (state != CycleState.OPEN) {
            throw DomainException.conflict("Only an OPEN cycle can be closed.");
        }
        this.state = CycleState.CLOSED;
        this.closedAt = when;
        touch();
    }

    public boolean isOpen() {
        return state == CycleState.OPEN;
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CycleState getState() {
        return state;
    }

    public Instant getOpenDate() {
        return openDate;
    }

    public Instant getCloseDate() {
        return closeDate;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }
}
