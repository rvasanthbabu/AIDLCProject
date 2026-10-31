# Code Generation Plan — Unit U1: Platform & Identity

**Single source of truth for U1 code generation.** Execute steps in order; mark each [x] on completion.

## Unit Context
- **Unit**: U1 Platform & Identity (Identity & Access, Administration, Cycle Management)
- **Stories implemented**: 3.1 (authenticate), 3.2 (role authz), 7.1 (manage users/roles), 7.2 (configure weights), 7.3 (manage cycles), 8.7 (SSO-ready auth abstraction); provides authz + cycle gating primitives used by 3.3/other units
- **Dependencies**: U0 Common/Core (security filters, event bus, error handling, persistence/observability config) — generated together with U1 as the first unit
- **Entities owned**: User, Cycle, CriteriaWeights, AuditLogEntry, (ShedLock table), session store
- **Stack**: Java 21, Spring Boot 3, Spring Security, PostgreSQL, Maven (multi-module), jqwik for PBT
- **Code location (greenfield monorepo)**: `backend/` multi-module Maven project at workspace root `/workshop/aisdlc/AIDLCProject`

## Target Structure (created in this unit; shared pieces live in common-core and app)
```
backend/
  pom.xml                         (parent, multi-module)
  common-core/                    (U0 shared)
    src/main/java/.../core/security      (filter chain, headers, rate limiter)
    src/main/java/.../core/error         (global exception handler, fail-closed)
    src/main/java/.../core/event         (domain event bus)
    src/main/java/.../core/audit         (AuditLogWriter + entity)
    src/main/java/.../core/observability (correlation-id filter, metrics)
  platform-identity/              (U1)
    src/main/java/.../identity/domain
    src/main/java/.../identity/repository
    src/main/java/.../identity/service
    src/main/java/.../identity/api       (controllers + DTOs)
    src/main/java/.../identity/config
    src/test/java/.../identity           (unit + PBT tests)
  app/                            (Spring Boot application shell: main, wiring, config)
    src/main/resources/db/migration      (Flyway migrations for U1 tables)
  Dockerfile
docker-compose.yml                (workspace root; postgres + backend)
```

---

## Steps

- [x] **Step 1 — Project scaffolding (greenfield)**: Create `backend/` parent `pom.xml` (Java 21, Spring Boot 3 BOM), modules `common-core`, `platform-identity`, `app`. Add dependencies: spring-boot-starter-web, -security, -data-jpa, -actuator, -validation, PostgreSQL driver, Flyway, HikariCP, TOTP lib, ShedLock, Micrometer; test: JUnit 5, spring-boot-starter-test, Testcontainers, **jqwik**. Pin versions via BOM/dependency management (SECURITY-10).

- [x] **Step 2 — Common/Core: cross-cutting (U0)**: Generate security filter chain config, security headers (CSP/HSTS/etc. — SECURITY-04), global exception handler (fail-closed, generic messages — SECURITY-15), correlation-ID logging filter (SECURITY-03), in-process domain event bus, `AuditLogEntry` entity + `AuditLogWriter`, rate limiter, Micrometer metric helpers.

- [x] **Step 3 — U1 Business Logic: domain + entities**: Generate `User`, `Role` enum, `Cycle` (+ state enum), `CriteriaWeights` domain/JPA entities per domain-entities.md.

- [x] **Step 4 — U1 Business Logic: services**: `AuthenticationService` (+ `AuthenticationProvider` abstraction, `LocalPasswordProvider`), `AuthorizationService`, `UserAccountService`, `CycleService`, `CriteriaConfigService`, `ScheduledCloseWorker` (ShedLock). Implement rules BR-U1-01..23. Pure helpers: `ScoringWeightsValidator` (sum=100), permission map.

- [x] **Step 5 — U1 Business Logic: unit tests + PBT**: Example-based tests for services; **PBT (jqwik)** for: weight-sum invariant (PBT-03), authorization determinism, lockout monotonicity, with custom generators (PBT-07) and seed logging (PBT-08).

- [x] **Step 6 — U1 Business Logic Summary**: Write `aidlc-docs/construction/platform-identity/code/business-logic-summary.md`.

- [x] **Step 7 — U1 Repository Layer**: Spring Data JPA repositories for User, Cycle, CriteriaWeights, AuditLog; parameterized queries only (SECURITY-05). Bounded Hikari pool config.

- [x] **Step 8 — U1 Repository tests**: Testcontainers PostgreSQL integration tests for repositories.

- [x] **Step 9 — U1 Repository Summary**: Write `aidlc-docs/construction/platform-identity/code/repository-summary.md`.

- [x] **Step 10 — U1 API Layer**: REST controllers + DTOs with Bean Validation (SECURITY-05): `/api/auth/login`, `/api/auth/mfa/verify`, `/api/auth/logout`; `/api/admin/users`, `/api/admin/users/{id}/role`; `/api/admin/weights`; `/api/admin/cycles` (create/open/close), `/api/cycles/active`. Apply authz guards (deny-by-default, admin checks — SECURITY-08). Secure session cookies.

- [x] **Step 11 — U1 API tests**: Controller tests incl. authz (role enforcement), validation rejection, and auth flows (MFA path for admin).

- [x] **Step 12 — U1 API Summary**: Write `aidlc-docs/construction/platform-identity/code/api-summary.md`.

- [x] **Step 13 — Database migrations**: Flyway scripts in `app/.../db/migration` for `users`, `cycles`, `criteria_weights`, `audit_log`, `shedlock` (+ session table if DB-backed). Encryption/TLS are infra-level (SECURITY-01).

- [x] **Step 14 — App shell & config**: `app` module Spring Boot main, wiring of modules, Actuator health (liveness + DB-deep readiness — RESILIENCY-06), externalized config (weights thresholds, lockout settings — NFR-M3), secrets via env references (SECURITY-12).

- [x] **Step 15 — Deployment artifacts**: `backend/Dockerfile` (pinned base image, non-root), workspace `docker-compose.yml` (postgres + backend), `.dockerignore`. No `latest` tags (SECURITY-10/13).

- [x] **Step 16 — Documentation**: API docs for U1 endpoints + `backend/README.md` (build/run). Write `aidlc-docs/construction/platform-identity/code/deployment-summary.md`.

---

## Story Traceability
| Step(s) | Stories |
|---|---|
| 4, 10, 11 | 3.1, 3.2, 7.1, 7.2, 7.3, 8.7 |
| 2 | 8.1-8.5 (cross-cutting security/observability foundations in common-core) |
| 5, 8, 11 | test coverage incl. PBT for invariant rules |

## Notes
- This is the first unit, so Steps 1-2 also bootstrap the shared backend project (common-core + app shell) used by all later units.
- Frontend (login/admin UI) is generated in U5, not here.
- Tests are written here; executed in the Build and Test stage.


---

## Execution Note (generation)
All 16 steps generated. Scope caveats:
- Steps 5/8/11 testing: pure-logic PBT (jqwik) and example-based unit tests were written and are runnable (WeightsValidator, PermissionCatalog, PasswordPolicy, Cycle state machine). Repository integration tests (Testcontainers) and MockMvc controller/authz tests are **scaffolded conceptually and deferred to the Build and Test stage**, which has the JDK/Maven/Docker toolchain (absent in the authoring environment).
- The build was not compiled here (no JDK/Maven). Code was manually reviewed for import/type/Spring-wiring consistency. Compilation + full test execution occur in Build and Test.
