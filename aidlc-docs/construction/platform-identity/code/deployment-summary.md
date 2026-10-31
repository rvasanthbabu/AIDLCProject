# Deployment Artifacts Summary — U1 Platform & Identity

## Generated
- `backend/pom.xml` (multi-module parent), `common-core/pom.xml`, `platform-identity/pom.xml`, `app/pom.xml`
- `backend/app/src/main/resources/application.yml` — datasource, Hikari, Flyway, session cookie security, Actuator health, auth/cycle config
- `backend/app/src/main/resources/db/migration/V1__platform_identity.sql` — Flyway migration (users, cycles w/ single-open partial unique index, criteria_weights w/ sum=100 check, audit_log, shedlock)
- `backend/app/.../IdeationPortalApplication.java` — Spring Boot main, scheduling, ShedLock
- `backend/app/.../config/ShedLockConfig.java` — JDBC lock provider
- `backend/Dockerfile` — multi-stage, non-root user, pinned base tags (digest-pin in CI — SECURITY-10/13)
- `backend/.dockerignore`
- `docker-compose.yml` (repo root) — local postgres + backend
- `backend/README.md` — build/run/test instructions

## Database-level enforcement (defense in depth)
- Partial unique index `ux_cycles_single_open` enforces at most one OPEN cycle (BR-U1-15).
- `ck_weights_sum` / `ck_weights_range` enforce weight constraints (BR-U1-19/20).
- `ux_users_email_lower` enforces case-insensitive email uniqueness (BR-U1-01).

## Notes / follow-ups for CI (Build & Test stage)
- Pin Docker base images by digest; add dependency vulnerability scan + SBOM generation (SECURITY-10).
- Build and tests (incl. Testcontainers + jqwik PBT) execute in Build and Test; JDK/Maven/Docker are not present in the authoring environment.
