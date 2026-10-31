# Ideation Portal — Backend

Modular monolith (Spring Boot 3, Java 21) packaged as a single deployable. Unit U1 (Platform & Identity) is implemented; later units (Ideas, Evaluation & Scoring, Insights & Recognition) are added as modules.

## Modules
- `common-core` — cross-cutting: security headers, correlation-id logging, global error handling, domain-event bus, audit log, rate limiter.
- `platform-identity` — U1: authentication (email/password, TOTP MFA for admins), authorization, user/role management, cycles, criteria weights.
- `app` — application shell: wiring, Flyway migrations, scheduling, ShedLock.

## Prerequisites
- JDK 21, Maven 3.9+
- Docker (for local PostgreSQL / full stack)

## Build
```
cd backend
mvn -ntp clean package
```

## Run tests (unit + property-based)
```
cd backend
mvn -ntp test
```

## Run locally (full stack)
From the repository root:
```
docker compose up --build
```
Backend: http://localhost:8080 — health at `/actuator/health`.

## Key endpoints (U1)
- `POST /api/auth/login` — email/password login (returns mfaRequired for admins)
- `POST /api/auth/mfa/verify` — complete MFA
- `POST /api/auth/logout`
- `POST /api/admin/users`, `PUT /api/admin/users/{id}/role` — ADMINISTRATOR
- `GET/PUT /api/admin/weights` — read any authenticated; update ADMINISTRATOR
- `POST /api/cycles`, `POST /api/cycles/{id}/open`, `POST /api/cycles/{id}/close`, `GET /api/cycles/active`

## Security & configuration notes
- Session cookies are Secure/HttpOnly/SameSite; CSRF protection enabled. Behind TLS in all deployed environments.
- Secrets (DB password, etc.) are injected via environment and should come from a secrets manager in non-local environments.
- CORS is restricted to the configured frontend origin.
