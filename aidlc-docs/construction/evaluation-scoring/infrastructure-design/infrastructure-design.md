# Infrastructure Design — Unit U3: Evaluation & Scoring

Runs inside the shared backend deployable. See `shared-infrastructure.md`.

## Data Stores (shared PostgreSQL)
- `assignments` (unique (idea_id, panel_member_id); active flag) — FKs to ideas/users/cycles.
- `evaluations` (unique (idea_id, panel_member_id); metrics, normalized_score, comment) — FKs to ideas/users/cycles.
- Indexes: `evaluations(idea_id)`, `evaluations(cycle_id)`, `assignments(panel_member_id, cycle_id)`, `assignments(idea_id)`.
- Encrypted at rest + TLS (SECURITY-01).

## Networking
- Endpoints `/api/evaluations/**`, `/api/assignments/**` behind shared load balancer; panel/admin authenticated sessions. No new ingress.

## Scaling / Availability
- Stateless; inherits multi-zone. Aggregation queries index-backed.
- Deferred option: aggregate cache (e.g., managed distributed cache) if load tests require — not provisioned in v1.

## Deployment
- Part of the single backend image; same canary pipeline + auto-rollback.
