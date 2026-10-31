package com.ideationportal.identity.service;

import com.ideationportal.core.error.DomainException;
import com.ideationportal.identity.domain.Role;
import org.springframework.stereotype.Service;

/**
 * Function-level authorization (BR-U1-12/13; SECURITY-08). Deny by default.
 */
@Service
public class AuthorizationService {

    public boolean hasPermission(Role role, PermissionCatalog.Action action) {
        return PermissionCatalog.isAllowed(role, action);
    }

    /**
     * Throws FORBIDDEN if the role is not permitted for the action.
     */
    public void requirePermission(Role role, PermissionCatalog.Action action) {
        if (!hasPermission(role, action)) {
            throw DomainException.forbidden("You are not permitted to perform this action.");
        }
    }
}
