package com.ideationportal.core.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-process token-bucket rate limiter for public endpoints (SECURITY-11).
 * Keyed by an arbitrary string (e.g., client IP or account). For multi-instance clustered
 * limits a shared store would be used; this in-process limiter is the v1 baseline.
 */
@Component
public class RateLimiter {

    private static final int DEFAULT_CAPACITY = 10;
    private static final Duration DEFAULT_REFILL_WINDOW = Duration.ofMinutes(1);

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * @return true if the request is allowed, false if the limit is exceeded.
     */
    public boolean tryAcquire(String key) {
        return tryAcquire(key, DEFAULT_CAPACITY, DEFAULT_REFILL_WINDOW);
    }

    public boolean tryAcquire(String key, int capacity, Duration refillWindow) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(capacity, Instant.now()));
        synchronized (bucket) {
            Instant now = Instant.now();
            long elapsed = Duration.between(bucket.windowStart, now).toMillis();
            if (elapsed >= refillWindow.toMillis()) {
                bucket.tokens = capacity;
                bucket.windowStart = now;
            }
            if (bucket.tokens > 0) {
                bucket.tokens--;
                return true;
            }
            return false;
        }
    }

    private static final class Bucket {
        private int tokens;
        private Instant windowStart;

        private Bucket(int tokens, Instant windowStart) {
            this.tokens = tokens;
            this.windowStart = windowStart;
        }
    }
}
