# Deployment Architecture — Unit U3 (within shared platform)

## Text summary
```
Admin --/api/assignments--> Backend -> assignments table ; publishes IdeaAssignedEvent -> Notification (U4)
Panel --/api/evaluations--> Backend -> evaluations table (active-assignment + cycle-open checks)
Dashboard reads (U4) --> SQL group-by aggregation over evaluations (AVG/SUM/COUNT), index-backed
```

## Notes
- No new infrastructure; U3 compiles into the shared backend image.
- On-read aggregation performed in PostgreSQL via grouped queries; optional cache deferred and measured.
- Same observability, backups, secrets, and canary/auto-rollback as the platform.
