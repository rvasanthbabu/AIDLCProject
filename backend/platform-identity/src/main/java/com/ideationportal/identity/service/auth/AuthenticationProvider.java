package com.ideationportal.identity.service.auth;

import com.ideationportal.identity.domain.User;

import java.util.Optional;

/**
 * SSO-ready authentication abstraction (FR-9.2; Story 8.7). v1 ships a local password
 * provider; a future OIDC/SAML provider can be added without changing authorization.
 */
public interface AuthenticationProvider {

    /**
     * @return the authenticated user if credentials are valid, otherwise empty.
     */
    Optional<User> authenticate(String email, String rawPassword);

    String providerType();
}
