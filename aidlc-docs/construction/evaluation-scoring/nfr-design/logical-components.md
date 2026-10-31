# Logical Components — Unit U3: Evaluation & Scoring

| Logical Component | Source | Use in U3 |
|---|---|---|
| Security Filter Chain / authz | common-core + U1 | Role + active-assignment checks |
| Domain Event Bus | common-core | Publish IdeaAssignedEvent |
| Global Error Handler | common-core | Fail-closed responses |
| Audit Log Writer | common-core | Record assignment changes |
| Persistence / Pool | common-core / shared PostgreSQL | assignments, evaluations tables |

New within U3: pure scoring/aggregation/ranking functions (no infra component). Optional future: aggregate cache/materialized view (deferred, measured).
