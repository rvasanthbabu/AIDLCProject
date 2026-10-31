package com.ideationportal.core.audit;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Writes append-only audit entries. Callers must pass only non-sensitive details
 * (no passwords, tokens, or PII) (SECURITY-03/13/14).
 */
@Component
public class AuditLogWriter {

    private final AuditLogRepository repository;

    public AuditLogWriter(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void record(UUID actorUserId, String action, String targetRef, String details) {
        repository.save(new AuditLogEntry(actorUserId, action, targetRef, details, Instant.now()));
    }
}
