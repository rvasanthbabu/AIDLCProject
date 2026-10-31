package com.ideationportal.core.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Append-only audit record (SECURITY-13/14). Never store secrets or PII in {@code details}.
 */
@Entity
@Table(name = "audit_log")
public class AuditLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "actor_user_id")
    private UUID actorUserId;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(name = "target_ref", length = 200)
    private String targetRef;

    @Column(length = 2000)
    private String details;

    @Column(nullable = false)
    private Instant timestamp;

    protected AuditLogEntry() {
    }

    public AuditLogEntry(UUID actorUserId, String action, String targetRef, String details, Instant timestamp) {
        this.actorUserId = actorUserId;
        this.action = action;
        this.targetRef = targetRef;
        this.details = details;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public UUID getActorUserId() {
        return actorUserId;
    }

    public String getAction() {
        return action;
    }

    public String getTargetRef() {
        return targetRef;
    }

    public String getDetails() {
        return details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
