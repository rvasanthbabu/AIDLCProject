package com.ideationportal.identity.config;

import com.ideationportal.identity.service.PasswordPolicy;
import com.ideationportal.identity.service.PasswordPolicy.BreachedPasswordChecker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.ideationportal.identity.service.auth.AuthProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * Identity beans. BCrypt adaptive hashing (BR-U1-03). Breached-password checker is a simple
 * set-backed default in v1; replace with a k-anonymity list source in production.
 */
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class IdentityBeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public BreachedPasswordChecker breachedPasswordChecker() {
        // Minimal default seed list; production should load a curated/k-anonymity source.
        return new PasswordPolicy.SetBackedBreachedChecker(Set.of(
                "password", "password1", "12345678", "qwerty123", "letmein1", "admin123"));
    }
}
