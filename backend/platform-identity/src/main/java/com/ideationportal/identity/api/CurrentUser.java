package com.ideationportal.identity.api;

import com.ideationportal.identity.domain.Role;

import java.io.Serializable;
import java.util.UUID;

/**
 * Authenticated principal stored in the HTTP session's security context. Immutable and
 * free of secrets.
 */
public record CurrentUser(UUID userId, Role role, String displayName) implements Serializable {
}
