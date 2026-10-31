package com.ideationportal.identity.domain;

/**
 * Cycle lifecycle states. Transitions allowed: DRAFT -> OPEN -> CLOSED only (BR-U1-16).
 */
public enum CycleState {
    DRAFT,
    OPEN,
    CLOSED
}
