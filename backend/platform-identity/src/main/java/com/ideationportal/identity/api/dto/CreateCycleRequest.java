package com.ideationportal.identity.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateCycleRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull Instant openDate,
        @NotNull Instant closeDate) {
}
