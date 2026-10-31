package com.ideationportal.identity.service;

import com.ideationportal.identity.domain.Role;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Deny-by-default function-level permission map (BR-U1-12/13; SECURITY-08).
 * Pure/deterministic lookup -> property-tested for determinism.
 */
public final class PermissionCatalog {

    public enum Action {
        SUBMIT_IDEA,
        EVALUATE,
        VIEW_DASHBOARD,
        MANAGE_USERS,
        CONFIGURE_WEIGHTS,
        MANAGE_CYCLES,
        ASSIGN_IDEAS,
        TRIGGER_RECOGNITION
    }

    private static final Map<Action, Set<Role>> ALLOWED = Map.of(
            Action.SUBMIT_IDEA, EnumSet.of(Role.SUBMITTER),
            Action.EVALUATE, EnumSet.of(Role.PANEL_MEMBER),
            Action.VIEW_DASHBOARD, EnumSet.of(Role.SUBMITTER, Role.PANEL_MEMBER, Role.ADMINISTRATOR, Role.VIEWER),
            Action.MANAGE_USERS, EnumSet.of(Role.ADMINISTRATOR),
            Action.CONFIGURE_WEIGHTS, EnumSet.of(Role.ADMINISTRATOR),
            Action.MANAGE_CYCLES, EnumSet.of(Role.ADMINISTRATOR),
            Action.ASSIGN_IDEAS, EnumSet.of(Role.ADMINISTRATOR),
            Action.TRIGGER_RECOGNITION, EnumSet.of(Role.ADMINISTRATOR)
    );

    private PermissionCatalog() {
    }

    /**
     * @return true iff the role is explicitly allowed for the action (deny by default).
     */
    public static boolean isAllowed(Role role, Action action) {
        if (role == null || action == null) {
            return false;
        }
        return ALLOWED.getOrDefault(action, Set.of()).contains(role);
    }
}
