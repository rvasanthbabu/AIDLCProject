package com.ideationportal.identity.event;

import com.ideationportal.core.event.DomainEvent;

import java.util.UUID;

/**
 * Published exactly once when a cycle transitions to CLOSED (BR-U1-18).
 * Consumed by Recognition (U4) to mark the cycle eligible for recognition.
 */
public record CycleClosedEvent(UUID cycleId) implements DomainEvent {
}
