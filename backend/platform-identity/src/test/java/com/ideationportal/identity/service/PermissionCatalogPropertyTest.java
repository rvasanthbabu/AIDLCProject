package com.ideationportal.identity.service;

import com.ideationportal.identity.domain.Role;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.ForType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Property-based tests for authorization determinism (PBT-03 invariant).
 */
class PermissionCatalogPropertyTest {

    /** Determinism: identical (role, action) always yield the same decision. */
    @Property
    void decisionIsDeterministic(@ForAll Role role,
                                 @ForAll PermissionCatalog.Action action) {
        boolean first = PermissionCatalog.isAllowed(role, action);
        boolean second = PermissionCatalog.isAllowed(role, action);
        assertThat(second).isEqualTo(first);
    }

    /** Null inputs deny by default. */
    @Property
    void nullDeniesByDefault(@ForAll PermissionCatalog.Action action) {
        assertThat(PermissionCatalog.isAllowed(null, action)).isFalse();
    }

    /** Only ADMINISTRATOR may configure weights (sample of deny-by-default rule). */
    @Property
    void onlyAdminConfiguresWeights(@ForAll Role role) {
        boolean allowed = PermissionCatalog.isAllowed(role, PermissionCatalog.Action.CONFIGURE_WEIGHTS);
        assertThat(allowed).isEqualTo(role == Role.ADMINISTRATOR);
    }
}
