# Repository Layer Summary — U2 Ideas

## Generated
- `IdeaRepository`: `findByIdAndSubmitterId` (IDOR-safe), paged `findBySubmitterId` / `findBySubmitterIdAndCycleId`, `findByCycleIdAndState` (for U4 reuse). All parameterized (SECURITY-05).

## Schema
- `V2__ideas.sql`: ideas table, FKs to users/cycles, indexes `ix_ideas_submitter` and `ix_ideas_cycle_state`.

## Notes
- Testcontainers repository integration tests run in Build and Test (Docker required; not available in authoring env).
