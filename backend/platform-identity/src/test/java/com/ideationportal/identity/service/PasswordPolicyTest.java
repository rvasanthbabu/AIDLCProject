package com.ideationportal.identity.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example-based tests for password complexity (BR-U1-02). Pins concrete known cases.
 */
class PasswordPolicyTest {

    @Test
    void rejectsTooShort() {
        assertThat(PasswordPolicy.meetsComplexity("Ab1!")).isFalse();
    }

    @Test
    void rejectsMissingSymbol() {
        assertThat(PasswordPolicy.meetsComplexity("Abcdefg1")).isFalse();
    }

    @Test
    void rejectsMissingDigit() {
        assertThat(PasswordPolicy.meetsComplexity("Abcdefg!")).isFalse();
    }

    @Test
    void acceptsCompliant() {
        assertThat(PasswordPolicy.meetsComplexity("Abcdef1!")).isTrue();
    }

    @Test
    void rejectsNull() {
        assertThat(PasswordPolicy.meetsComplexity(null)).isFalse();
    }
}
