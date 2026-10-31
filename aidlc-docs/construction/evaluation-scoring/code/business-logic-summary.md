# Business Logic Summary — U3 Evaluation & Scoring

## Generated (backend/evaluation-scoring)
### Pure scoring core (no framework deps — primary PBT target)
- `scoring/ScoreCalculator` — weightedSum + normalizedScore = (weightedSum-100)/400*100 (BigDecimal), validates metrics 1-5 and weights sum=100
- `scoring/Aggregator` — mean of normalized scores + per-metric SUMs + count
- `scoring/IdeaRanker` — deterministic comparator (score DESC, submittedAt ASC, ideaId ASC), rank + topN
- `scoring/AggregateResult`, `scoring/RankedIdea`

### Domain / services
- `domain/Assignment` (deactivatable), `domain/Evaluation` (recomputes normalizedScore on change)
- `repository/AssignmentRepository`, `repository/EvaluationRepository` (+ SQL AVG/SUM/COUNT aggregation queries)
- `service/AssignmentService` (role check via U1 UserAccountService.getRole, idea->UnderEvaluation via U2, publishes IdeaAssignedEvent, keep-eval-on-remove)
- `service/EvaluationService` (active-assignment + cycle-open + validation, independence read rule, mark-SCORED on first eval)
- `service/ScoringQueryService` (per-idea aggregate; bridges EvaluationRepository to U4 for cycle ranking)
- `event/IdeaAssignedEvent`

### Added to U1
- `UserAccountService.getRole(userId)` read-only lookup for cross-unit role checks.

## Rules implemented
BR-U3-01..26 (assignment admin-only + panel-role, idea gating, keep-eval-on-remove, scoring validation, independence, scored-eligibility, deterministic ranking, transactional/parameterized, fail-closed).

## PBT (partial — strongest unit)
- `ScoreCalculatorPropertyTest` — bounds [0,100], extremes map 0/100, monotonicity (PBT-03)
- `AggregatorPropertyTest` — mean within input bounds, single=itself, empty=null (PBT-03)
- `EvaluationResponseRoundTripPropertyTest` — JSON round-trip (PBT-02)
- Example-based `IdeaRankerTest` — ordering + tie-break determinism

## Notes
- Scoring core is pure/deterministic (BigDecimal) for reliable property assertions.
- Build/tests run in Build and Test (no toolchain here); manually reviewed.
- `ScoringQueryService.repository()` is a deliberate read bridge for U4 cycle-wide ranking; may be replaced by a dedicated read API in U4.
