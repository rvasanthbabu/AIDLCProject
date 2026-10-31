package com.ideationportal.evaluation.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Admin assignment payload: assign an idea (within a cycle) to one or more panel members.
 */
public record AssignRequest(
        @NotNull UUID ideaId,
        @NotNull UUID cycleId,
        @NotEmpty List<UUID> panelMemberIds) {
}
