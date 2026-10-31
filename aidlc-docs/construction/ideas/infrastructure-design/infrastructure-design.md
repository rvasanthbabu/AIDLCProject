# Infrastructure Design — Unit U2: Ideas

U2 runs inside the shared backend deployable. See `aidlc-docs/construction/shared-infrastructure.md` for platform topology. U2-specific points:

## Data Stores (shared PostgreSQL)
- `ideas` table owned by U2 (encrypted at rest, TLS in transit — SECURITY-01).
- Indexes: by `submitter_id`; composite `(cycle_id, state)` to support dashboard/leaderboard reads in U4.

## Networking
- Idea endpoints `/api/ideas/**` behind the shared load balancer; all require an authenticated SUBMITTER session; no new ingress.

## Scaling / Availability
- Stateless; inherits multi-zone backend + multi-zone PostgreSQL. No U2-specific infra.

## Deployment
- Part of the single backend image; same canary pipeline and auto-rollback as the platform.
