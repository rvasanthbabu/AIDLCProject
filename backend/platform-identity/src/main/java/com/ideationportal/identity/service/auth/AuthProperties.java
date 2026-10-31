package com.ideationportal.identity.service.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Externalized auth policy configuration (NFR-M3). Defaults are safe; override per environment.
 */
@ConfigurationProperties(prefix = "ideation.auth")
public class AuthProperties {

    /** Consecutive failed logins before lockout (BR-U1-04). */
    private int lockoutThreshold = 5;

    /** Lockout cooldown duration. */
    private Duration lockoutDuration = Duration.ofMinutes(15);

    /** Rate-limit capacity per window for login attempts. */
    private int loginRateCapacity = 10;

    public int getLockoutThreshold() {
        return lockoutThreshold;
    }

    public void setLockoutThreshold(int lockoutThreshold) {
        this.lockoutThreshold = lockoutThreshold;
    }

    public Duration getLockoutDuration() {
        return lockoutDuration;
    }

    public void setLockoutDuration(Duration lockoutDuration) {
        this.lockoutDuration = lockoutDuration;
    }

    public int getLoginRateCapacity() {
        return loginRateCapacity;
    }

    public void setLoginRateCapacity(int loginRateCapacity) {
        this.loginRateCapacity = loginRateCapacity;
    }
}
