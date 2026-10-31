package com.ideationportal.identity.service;

import java.util.Set;

/**
 * Password policy (BR-U1-02; Q6=C): min length + complexity, no forced rotation.
 * Breached-password checking is delegated to {@link BreachedPasswordChecker}.
 * Pure validation, suitable for property/example testing.
 */
public final class PasswordPolicy {

    public static final int MIN_LENGTH = 8;

    private PasswordPolicy() {
    }

    public static boolean meetsComplexity(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }
        boolean upper = false, lower = false, digit = false, symbol = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                upper = true;
            } else if (Character.isLowerCase(c)) {
                lower = true;
            } else if (Character.isDigit(c)) {
                digit = true;
            } else {
                symbol = true;
            }
        }
        return upper && lower && digit && symbol;
    }

    /**
     * Functional interface for breached-password lookups (privacy-safe, offline/k-anonymity).
     */
    public interface BreachedPasswordChecker {
        boolean isBreached(String password);
    }

    /** Simple default backed by an in-memory set (replaceable with a k-anonymity list). */
    public static final class SetBackedBreachedChecker implements BreachedPasswordChecker {
        private final Set<String> breached;

        public SetBackedBreachedChecker(Set<String> breached) {
            this.breached = breached;
        }

        @Override
        public boolean isBreached(String password) {
            return breached.contains(password);
        }
    }
}
