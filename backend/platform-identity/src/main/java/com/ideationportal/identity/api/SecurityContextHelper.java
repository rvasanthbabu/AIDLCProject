package com.ideationportal.identity.api;

import com.ideationportal.core.error.DomainException;
import com.ideationportal.identity.domain.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * Helpers to read/establish the authenticated {@link CurrentUser} in the security context.
 * Token/session validation happens server-side on every request via Spring Security.
 */
public final class SecurityContextHelper {

    private SecurityContextHelper() {
    }

    public static Authentication buildAuthentication(CurrentUser user) {
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.role().name())));
    }

    public static void establish(CurrentUser user) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(buildAuthentication(user));
        SecurityContextHolder.setContext(context);
    }

    public static CurrentUser require() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CurrentUser cu)) {
            throw DomainException.unauthorized("Authentication required.");
        }
        return cu;
    }

    public static void requireRole(Role role) {
        if (require().role() != role) {
            throw DomainException.forbidden("You are not permitted to perform this action.");
        }
    }
}
