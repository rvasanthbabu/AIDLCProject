# Infrastructure Design — Unit U1: Platform & Identity

U1 runs inside the shared backend deployable. See `aidlc-docs/construction/shared-infrastructure.md` for the platform-wide topology (compute, networking, PostgreSQL, CI/CD, observability, secrets). This document covers U1-specific infrastructure mappings.

## U1 Logical-to-Infrastructure Mapping

| Logical Component | Infrastructure Realization |
|---|---|
| Security Filter Chain | In-process (Spring Security) within backend containers; no separate infra |
| Session Store | Backend-shared store: PostgreSQL table (v1 default) OR managed distributed cache if latency requires — chosen platform-wide, see shared doc |
| Rate Limiter | In-process token-bucket (v1); may move to gateway/cache-backed if clustered limits needed |
| Password Encoder / TOTP Provider | In-process libraries; MFA secret stored via Secrets Accessor → secrets manager |
| Secrets Accessor | Reads DB creds, MFA secrets from the platform secrets manager |
| Audit Log Writer | Writes to PostgreSQL append-only `audit_log` table; log stream also shipped to centralized logging |
| Scheduled Close Worker | Runs inside backend; ShedLock uses a PostgreSQL lock table so only one instance closes a cycle |
| Health Indicators | Actuator endpoints consumed by the load balancer health checks and orchestrator probes |

## U1 Data Stores (owned tables in shared PostgreSQL)
- `users`, `cycles`, `criteria_weights`, `audit_log`, `shedlock` (lock table), and session table if DB-backed.
- All encrypted at rest; accessed over TLS (SECURITY-01).

## U1-Specific Networking
- Auth endpoints (`/api/auth/*`) are public (through the load balancer) but rate-limited; all other U1 admin endpoints require authenticated admin sessions.
- No additional ingress beyond the shared load balancer.

## U1 Scaling Notes
- U1 logic is stateless (session externalized), so it scales with the backend replica count.
- The scheduled close worker is singleton-effECTIVE via ShedLock regardless of replica count.

## U1 Availability
- Inherits multi-zone backend + multi-zone PostgreSQL (RESILIENCY-08). Auth availability equals platform availability (highest criticality).
