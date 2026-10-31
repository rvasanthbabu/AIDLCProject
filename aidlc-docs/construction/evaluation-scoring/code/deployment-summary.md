# Deployment Summary — U3 Evaluation & Scoring

- New Maven module `backend/evaluation-scoring` added to parent pom + app dependencies (single deployable).
- Flyway `V3__evaluation_scoring.sql` adds assignments + evaluations tables, unique constraints, indexes, and metric CHECK.
- No new infrastructure; same canary pipeline, observability, backups.
- Deferred/optional: aggregate cache or materialized view if load tests require (documented in NFR).
