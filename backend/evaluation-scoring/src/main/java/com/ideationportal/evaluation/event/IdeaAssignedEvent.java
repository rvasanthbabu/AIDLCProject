package com.ideationportal.evaluation.event;

import com.ideationportal.core.event.DomainEvent;

import java.util.List;
import java.util.UUID;

/**
 * Published when an admin assigns an idea to one or more panel members (Story 3.3).
 * Consumed by Notification (U4) to email the panel members.
 */
public record IdeaAssignedEvent(UUID ideaId, List<UUID> panelMemberIds, UUID cycleId) implements DomainEvent {
}
