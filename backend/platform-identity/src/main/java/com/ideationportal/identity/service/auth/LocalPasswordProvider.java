package com.ideationportal.identity.service.auth;

import com.ideationportal.identity.domain.User;
import com.ideationportal.identity.domain.UserStatus;
import com.ideationportal.identity.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Local email/password authentication using an adaptive {@link PasswordEncoder} (BR-U1-03).
 * Returns empty on any failure; never reveals whether the email exists (BR-U1-05).
 */
@Component
public class LocalPasswordProvider implements AuthenticationProvider {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public LocalPasswordProvider(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> authenticate(String email, String rawPassword) {
        Optional<User> maybeUser = users.findByEmailIgnoreCase(email);
        if (maybeUser.isEmpty()) {
            return Optional.empty();
        }
        User user = maybeUser.get();
        if (user.getStatus() != UserStatus.ACTIVE) {
            return Optional.empty();
        }
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public String providerType() {
        return "local";
    }
}
