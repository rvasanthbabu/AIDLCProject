# Tech Stack Decisions — Unit U1: Platform & Identity

These apply to U1 and, where shared, to all backend units (consistent stack across the modular monolith).

## Core Stack (from requirements)
- **Language/Runtime**: Java 21 (LTS) — current, supported (SECURITY-09).
- **Framework**: Spring Boot 3.x (Spring MVC for REST, Spring Security for auth/authz).
- **Database**: PostgreSQL (multi-zone).
- **Build**: Maven (multi-module) — reproducible, lockable via dependency management.
- **Packaging**: Docker (pinned base image, no `latest` — SECURITY-10/13).

## U1-Specific Libraries
- **Spring Security** — authentication filter chain, authorization, security headers.
- **Password hashing**: BCrypt (adaptive) via Spring Security `PasswordEncoder` (BR-U1-03). (Argon2 optional alternative; BCrypt chosen for maturity/simplicity.)
- **MFA (TOTP)**: a TOTP library (e.g., `java-otp` / `dev.samstevens.totp`) for RFC 6238 codes.
- **Breached-password check**: offline k-anonymity list or local dictionary check (no external call that leaks password; privacy-safe).
- **Input validation**: Jakarta Bean Validation (`jakarta.validation`).
- **JWT/session**: server-side session (opaque token in Secure/HttpOnly/SameSite cookie) with server-side store; JWT optional later for SSO.
- **Scheduling**: Spring `@Scheduled` with a DB-backed lock (e.g., ShedLock) for the idempotent cycle auto-close job (U1-NFR-REL-3).

## Testing Stack
- **Unit/integration**: JUnit 5, Spring Boot Test, Testcontainers (PostgreSQL) for repository/integration tests.
- **Property-based testing**: **jqwik** (PBT-09) — integrates with JUnit 5, supports custom generators + shrinking + seeds (PBT-07/08).

## Secrets & Config
- Secrets via environment-injected secrets manager references (no hardcoded creds — SECURITY-12).
- Configuration externalized (Spring profiles / config server or env vars).

## Rationale Notes
- Single consistent backend stack simplifies the modular monolith build and ops.
- Spring Security provides battle-tested implementations for most Security Baseline controls (headers, CSRF, session, authz), reducing custom security code (SECURITY-11 defense in depth).
