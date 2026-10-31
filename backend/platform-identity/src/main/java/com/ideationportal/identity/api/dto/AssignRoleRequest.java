package com.ideationportal.identity.api.dto;

import com.ideationportal.identity.domain.Role;
import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(@NotNull Role role) {
}
