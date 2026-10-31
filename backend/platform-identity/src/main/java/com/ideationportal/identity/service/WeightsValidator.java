package com.ideationportal.identity.service;

/**
 * Pure validation of criteria weights (BR-U1-19/20). Isolated as a pure function to enable
 * property-based testing (PBT-03 invariant: accepted iff each in [0,100] and sum == 100).
 */
public final class WeightsValidator {

    public static final int REQUIRED_SUM = 100;
    public static final int MIN = 0;
    public static final int MAX = 100;

    private WeightsValidator() {
    }

    public static boolean isValid(int feasibility, int impact, int innovation) {
        return inRange(feasibility) && inRange(impact) && inRange(innovation)
                && (feasibility + impact + innovation) == REQUIRED_SUM;
    }

    private static boolean inRange(int w) {
        return w >= MIN && w <= MAX;
    }
}
