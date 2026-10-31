# NFR Design Patterns — Unit U3: Evaluation & Scoring

## Security
- Active-assignment guard on all panel read/write (object-level; SECURITY-08).
- Independence rule in read methods: peer evaluations hidden pre-submission (Q6=B).
- Metric range validation (1-5) at DTO + domain; parameterized queries (SECURITY-05).

## Performance (on-read aggregation)
- Aggregation and ranking computed via SQL group-by (AVG normalizedScore, SUM per metric, COUNT) rather than loading all rows into memory.
- Indexes: `evaluations(idea_id)`, `evaluations(cycle_id)`, and reuse `ideas(cycle_id, state)`.
- Escape hatch (deferred, measured): short-TTL cache or materialized aggregate updated on evaluation write — only if load tests show the SQL path misses targets. Documented so U4/Build-and-Test can decide with data.

## Reliability / Determinism
- `BigDecimal` scoring; pure functions with no side effects (ideal for PBT).
- Ranking comparator is total and deterministic (score DESC, submittedAt ASC, ideaId ASC).
- Optimistic locking on Evaluation; transactional upserts.

## Logical Components
- Reuses common-core (security, error handling, events, logging). New within U3: none at infra level; scoring is pure code.
- Publishes `IdeaAssignedEvent` via common-core event bus.
