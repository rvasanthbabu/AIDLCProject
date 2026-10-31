package com.ideationportal.identity.domain;

import com.ideationportal.core.error.DomainException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Example-based tests for the cycle state machine (BR-U1-16/17).
 */
class CycleStateMachineTest {

    private Cycle newCycle() {
        Instant open = Instant.now();
        Instant close = open.plus(7, ChronoUnit.DAYS);
        return new Cycle("Q3 Innovation", open, close, UUID.randomUUID());
    }

    @Test
    void rejectsCloseDateBeforeOpenDate() {
        Instant open = Instant.now();
        assertThatThrownBy(() -> new Cycle("bad", open, open.minusSeconds(1), UUID.randomUUID()))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void draftCanOpenThenClose() {
        Cycle c = newCycle();
        c.open();
        assertThat(c.isOpen()).isTrue();
        c.close(Instant.now());
        assertThat(c.getState()).isEqualTo(CycleState.CLOSED);
        assertThat(c.getClosedAt()).isNotNull();
    }

    @Test
    void cannotOpenNonDraft() {
        Cycle c = newCycle();
        c.open();
        assertThatThrownBy(c::open).isInstanceOf(DomainException.class);
    }

    @Test
    void cannotCloseNonOpen() {
        Cycle c = newCycle();
        assertThatThrownBy(() -> c.close(Instant.now())).isInstanceOf(DomainException.class);
    }
}
