# Logical Components — Unit U1: Platform & Identity

Logical (infrastructure-adjacent) components introduced to realize the NFR patterns. Most are provided by common-core and reused by all units.

| Logical Component | Purpose | Owner | Notes |
|---|---|---|---|
| Security Filter Chain | AuthN, AuthZ, security headers, CSRF | common-core | Spring Security; applied app-wide |
| Rate Limiter | Throttle public auth endpoints | common-core | Token-bucket; keyed by IP/account |
| Session Store | Server-side session persistence | common-core / infra | Shared store for stateless app tier (DB table or distributed cache) |
| Password Encoder | Adaptive hashing | U1 | BCrypt via Spring Security |
| TOTP Provider | MFA code generation/verification | U1 | RFC 6238 |
| Secrets Accessor | Retrieve secrets at runtime | common-core / infra | Reads from secrets manager; no hardcoding |
| Audit Log Writer | Append-only security/critical event log | common-core | Writes AuditLogEntry; tamper-evident storage |
| Scheduled Close Worker | Auto-close cycles at closeDate | U1 | ShedLock-guarded, idempotent, publishes CycleClosedEvent |
| Domain Event Bus | In-process publish/subscribe | common-core | Spring ApplicationEventPublisher; events after commit |
| Health Indicators | Liveness + readiness (DB deep check) | common-core | Actuator |
| Metrics Registry | Counters/timers for auth & authz | common-core | Micrometer |
| Connection Pool | Bounded DB connections | common-core / infra | HikariCP |

## Integration Notes
- The **Domain Event Bus** publishes events only after the surrounding transaction commits (so a rolled-back close does not emit CycleClosedEvent).
- The **Session Store** choice (DB table vs distributed cache) is finalized in Infrastructure Design; logic is agnostic.
- Rate limiter and audit writer live in common-core so every unit inherits consistent behavior.
