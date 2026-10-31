# Unit Dependency Matrix & Build Order — Ideation Portal

## Dependency Matrix

"X" = the row unit depends on the column unit.

| Unit \ Depends on | U0 Common/Core | U1 Platform & Identity | U2 Ideas | U3 Evaluation & Scoring | U4 Insights & Recognition |
|---|---|---|---|---|---|
| U0 Common/Core | — | | | | |
| U1 Platform & Identity | X | — | | | |
| U2 Ideas | X | X | — | | |
| U3 Evaluation & Scoring | X | X | X | — | |
| U4 Insights & Recognition | X | X | X | X | — |
| U5 Frontend | (API) | (API) | (API) | (API) | (API) |

Notes:
- All backend units depend on **U0 Common/Core**.
- **U5 Frontend** depends on the backend units only through their HTTP APIs (not compile-time).
- No circular dependencies: dependencies flow strictly U0 → U1 → U2 → U3 → U4, with Frontend consuming all via API.

## Communication Patterns
- **Compile-time**: backend modules depend on U0 and on lower units' public service interfaces within the single deployable.
- **Runtime (in-process)**: synchronous service calls + internal domain events via U0's event bus.
- **Runtime (network)**: Frontend ↔ Backend over HTTPS REST (command + read/query APIs).

## Recommended Build / Development Order (Q4=C — AI recommended)

Dependency-ordered, which also minimizes rework:

1. **U0 Common/Core** — foundation (security, events, persistence, error handling) required by all.
2. **U1 Platform & Identity** — identity/authz, cycles, admin config; unblocks everything else.
3. **U2 Ideas** — needs identity + cycle gating.
4. **U3 Evaluation & Scoring** — needs ideas to evaluate and identity/cycles; delivers scores/ranking.
5. **U4 Insights & Recognition** — needs scores/aggregation; dashboards, recognition, notifications.
6. **U5 Frontend** — integrates against the completed backend APIs (can begin screen scaffolding earlier against API contracts, finalized after backend units stabilize).

### Text sequence
```
U0 -> U1 -> U2 -> U3 -> U4 -> U5
```

## Integration Checkpoints
- After U1: auth + cycle gating verifiable end-to-end (login, role checks, open/close cycle).
- After U2: submit/draft flow verifiable.
- After U3: assignment + scoring + aggregation/ranking verifiable.
- After U4: dashboards, recognition, notifications verifiable.
- After U5: full user journeys per persona verifiable (covered in Build and Test / integration).

## Rollback / Risk Notes
- Because this is a single deployable, units integrate continuously; canary deployment with auto-rollback (NFR-R9) governs production releases of the combined artifact.
