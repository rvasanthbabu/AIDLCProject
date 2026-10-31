package com.ideationportal.identity.api;

import com.ideationportal.identity.api.dto.AssignRoleRequest;
import com.ideationportal.identity.api.dto.CreateUserRequest;
import com.ideationportal.identity.domain.Role;
import com.ideationportal.identity.domain.User;
import com.ideationportal.identity.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * Administrator user/role management (Story 7.1). ADMINISTRATOR only, enforced server-side
 * via @PreAuthorize (function-level authorization, SECURITY-08).
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMINISTRATOR')")
public class AdminUserController {

    private final UserAccountService userAccountService;

    public AdminUserController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody CreateUserRequest request) {
        CurrentUser actor = SecurityContextHelper.require();
        User created = userAccountService.createUser(
                actor.userId(), request.email(), request.password(), request.displayName(), request.role());
        return ResponseEntity.ok(view(created));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<Map<String, Object>> assignRole(@PathVariable UUID id,
                                                          @Valid @RequestBody AssignRoleRequest request) {
        CurrentUser actor = SecurityContextHelper.require();
        User updated = userAccountService.assignRole(actor.userId(), id, request.role());
        return ResponseEntity.ok(view(updated));
    }

    private Map<String, Object> view(User u) {
        // Never expose passwordHash or mfaSecret.
        return Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "displayName", u.getDisplayName(),
                "role", u.getRole(),
                "mfaEnabled", u.isMfaEnabled(),
                "status", u.getStatus());
    }
}
