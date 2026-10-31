# Deployment Architecture — Unit U2 (within shared platform)

U2 deploys as part of the shared backend container; no separate deployment unit.

## Text summary
```
Users (Submitters) --HTTPS--> Load Balancer --/api/ideas/*--> Backend containers (AZ-a/AZ-b)
Backend -> PostgreSQL (ideas table; indexes on submitter_id and (cycle_id, state))
Submit action -> AuditLogWriter (common-core) -> audit_log
```

## Notes
- No new infrastructure. Same observability, secrets, backups, and canary/auto-rollback as defined in shared-infrastructure.md.
- Idea indexes are chosen to serve both submitter "my ideas" queries and downstream dashboard aggregation (U4).
