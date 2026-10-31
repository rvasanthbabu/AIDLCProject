# Logical Components — Unit U2: Ideas

U2 introduces no new infrastructure-level logical components; it reuses common-core.

| Logical Component | Source | Use in U2 |
|---|---|---|
| Security Filter Chain / authz | common-core + U1 | Role + session validation |
| Global Error Handler | common-core | Fail-closed responses |
| Audit Log Writer | common-core | Record idea submission |
| Structured Logging / Correlation | common-core | Request tracing |
| Persistence / Connection Pool | common-core / shared PostgreSQL | Idea table access |

New within U2: none (domain services + repository only).
