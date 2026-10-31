package com.ideationportal.identity.service;

import com.ideationportal.core.audit.AuditLogWriter;
import com.ideationportal.core.error.DomainException;
import com.ideationportal.core.event.DomainEventPublisher;
import com.ideationportal.identity.domain.Cycle;
import com.ideationportal.identity.domain.CycleState;
import com.ideationportal.identity.event.CycleClosedEvent;
import com.ideationportal.identity.repository.CycleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cycle lifecycle (Story 7.3). Enforces single-open-cycle invariant (BR-U1-15),
 * manual/auto close (BR-U1-17), and publishes CycleClosedEvent once (BR-U1-18).
 */
@Service
public class CycleService {

    private final CycleRepository cycles;
    private final DomainEventPublisher events;
    private final AuditLogWriter audit;

    public CycleService(CycleRepository cycles, DomainEventPublisher events, AuditLogWriter audit) {
        this.cycles = cycles;
        this.events = events;
        this.audit = audit;
    }

    @Transactional
    public Cycle createCycle(UUID actorId, String name, Instant openDate, Instant closeDate) {
        Cycle cycle = new Cycle(name, openDate, closeDate, actorId);
        Cycle saved = cycles.save(cycle);
        audit.record(actorId, "CYCLE_CREATED", "cycle:" + saved.getId(), name);
        return saved;
    }

    @Transactional
    public Cycle openCycle(UUID actorId, UUID cycleId) {
        if (cycles.existsByState(CycleState.OPEN)) {
            throw DomainException.conflict("Another cycle is already open. Close it before opening a new one.");
        }
        Cycle cycle = cycles.findById(cycleId)
                .orElseThrow(() -> DomainException.notFound("Cycle not found."));
        cycle.open();
        Cycle saved = cycles.save(cycle);
        audit.record(actorId, "CYCLE_OPENED", "cycle:" + cycleId, null);
        return saved;
    }

    @Transactional
    public Cycle closeCycle(UUID actorId, UUID cycleId) {
        Cycle cycle = cycles.findById(cycleId)
                .orElseThrow(() -> DomainException.notFound("Cycle not found."));
        return closeInternal(cycle, Instant.now(), actorId, "manual");
    }

    /**
     * Auto-close any OPEN cycle whose closeDate has passed. Idempotent and safe to run
     * from a single-instance scheduled worker (BR-U1-17).
     */
    @Transactional
    public int autoCloseDueCycles(Instant now) {
        List<Cycle> due = cycles.findByStateAndCloseDateLessThanEqual(CycleState.OPEN, now);
        for (Cycle cycle : due) {
            closeInternal(cycle, cycle.getCloseDate(), null, "auto");
        }
        return due.size();
    }

    @Transactional(readOnly = true)
    public Optional<Cycle> getActiveCycle() {
        return cycles.findFirstByState(CycleState.OPEN);
    }

    @Transactional(readOnly = true)
    public boolean isCycleOpen(UUID cycleId) {
        return cycles.findById(cycleId).map(Cycle::isOpen).orElse(false);
    }

    private Cycle closeInternal(Cycle cycle, Instant when, UUID actorId, String mode) {
        cycle.close(when);
        Cycle saved = cycles.save(cycle);
        // Publish exactly once per close (BR-U1-18). Subscribers act after commit.
        events.publish(new CycleClosedEvent(saved.getId()));
        audit.record(actorId, "CYCLE_CLOSED", "cycle:" + saved.getId(), "mode=" + mode);
        return saved;
    }
}
