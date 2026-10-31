# NFR Design Patterns — Unit U1: Platform & Identity

Patterns incorporated to satisfy U1 NFRs.

## Security Patterns
- **Filter-chain security (Spring Security)**: authN filter → authZ filter → security-headers filter applied to all requests. Deny-by-default (SECURITY-08).
- **Security headers**: CSP (`default-src 'self'`), HSTS (max-age 1yr, includeSubDomains), X-Content-Type-Options nosniff, X-Frame-Options DENY, Referrer-Policy strict-origin-when-cross-origin (SECURITY-04) — set centrally in common-core and reused.
- **Credential protection**: BCrypt adaptive hashing; MFA (TOTP) enforced for admins; secrets externalized (SECURITY-12).
- **Brute-force defense**: counter + lockout with progressive delay; rate limiting (token-bucket) on `/auth/login` (SECURITY-11/12).
- **Input validation**: Bean Validation annotations + explicit bounds on all DTOs; parameterized repository queries (SECURITY-05).
- **Audit logging**: append-only AuditLog via a dedicated writer; actor/action/timestamp; redaction of sensitive fields (SECURITY-13/14).

## Resilience Patterns
- **Timeouts**: DB statement/connection timeouts configured; no unbounded waits (RESILIENCY-10).
- **Fail-closed error handling**: global exception handler returns generic errors, rolls back transactions, denies on ambiguity (SECURITY-15).
- **Health checks**: Spring Boot Actuator `/health` (liveness shallow) + a readiness check verifying DB connectivity (deep) (RESILIENCY-06).
- **Idempotent scheduled close**: ShedLock ensures only one instance runs the auto-close; close operation is idempotent (re-running on an already-closed cycle is a no-op) and publishes CycleClosedEvent once.

## Scalability / Performance Patterns
- **Stateless app tier**: session state in a shared store (DB or distributed cache) so any instance serves any request; enables horizontal scaling across AZs.
- **In-process authorization**: role/permission checks computed from the validated principal without per-request remote calls (U1-NFR-PERF-1).
- **Connection pooling**: HikariCP with bounded pool to protect PostgreSQL (bulkhead against connection exhaustion, RESILIENCY-10).

## Observability Patterns
- **Structured logging** with correlation/request IDs via a servlet filter (common-core); no secrets/PII (SECURITY-03).
- **Metrics**: Micrometer counters/timers for login attempts, failures, lockouts, authz denials; exposed for the monitoring stack.
- **Security alerts**: metrics on repeated auth failures / authz violations feed alerting (SECURITY-14).

## Maintainability Patterns
- **Provider abstraction**: `AuthenticationProvider` interface (local now, SSO later) (FR-9.2).
- **Config-driven policy**: weights, lockout thresholds, password rules via externalized configuration (NFR-M3).
