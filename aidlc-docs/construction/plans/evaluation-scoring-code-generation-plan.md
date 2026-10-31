# Code Generation Plan — Unit U3: Evaluation & Scoring

**Single source of truth.** Execute in order; mark [x] on completion.

## Unit Context
- **Unit**: U3 Evaluation & Scoring
- **Stories**: 2.1, 2.2, 2.3, 2.4, 3.3 (assignment side), 6.1 (ranking input)
- **Dependencies**: U1 (CycleService, CriteriaConfigService for weights, roles, CurrentUser), U2 (IdeaService.markUnderEvaluation/markScored), common-core (events, audit, errors)
- **Entities owned**: Assignment, Evaluation. Value objects: AggregateResult, RankedIdea.
- **Module**: new `backend/evaluation-scoring/` (depends on common-core, platform-identity, ideas).
- **Package**: `com.ideationportal.evaluation`

## Target Structure
```
backend/evaluation-scoring/
  pom.xml
  src/main/java/com/ideationportal/evaluation/
    domain/      (Assignment, Evaluation)
    scoring/     (ScoreCalculator, Aggregator, IdeaRanker, RankedIdea, AggregateResult)  <- pure
    repository/  (AssignmentRepository, EvaluationRepository)
    service/     (AssignmentService, EvaluationService, ScoringQueryService)
    event/       (IdeaAssignedEvent)
    api/         (AssignmentController, EvaluationController, DTOs)
  src/test/java/... (PBT + example-based)
```

---

## Steps

- [x] **Step 1 — Module scaffolding**: `backend/evaluation-scoring/pom.xml`; add module to parent pom and to `app` dependencies.
- [x] **Step 2 — Pure scoring core**: `ScoreCalculator` (normalizedScore via (weightedSum-100)/400*100 with BigDecimal), `Aggregator` (mean of normalized, per-metric sums, count), `IdeaRanker` (comparator: score DESC, submittedAt ASC, ideaId ASC) + `AggregateResult`, `RankedIdea`. BR-U3-20..25.
- [x] **Step 3 — Pure scoring PBT + unit tests**: jqwik properties — normalized bounds [0,100], monotonicity, aggregate-within-bounds; example-based ranking/tie-break. (PBT-03; PBT-07/08 generators/seeds.)
- [x] **Step 4 — Domain entities**: `Assignment`, `Evaluation` (with recompute of normalizedScore).
- [x] **Step 5 — Repositories**: `AssignmentRepository`, `EvaluationRepository` incl. SQL aggregation queries (AVG normalized, SUM metrics, COUNT) — parameterized.
- [x] **Step 6 — Services**: `AssignmentService` (assign/remove, role check via U1, idea->UnderEvaluation via U2, publish IdeaAssignedEvent), `EvaluationService` (submit/update with active-assignment + cycle-open + 1-5 validation, independence read rules, mark idea SCORED on first evaluation via U2), `ScoringQueryService` (aggregateForIdea, rankIdeas). BR-U3-01..26.
- [x] **Step 7 — Service tests + PBT**: example-based (authz, independence, cycle gating, keep-eval-on-unassign) + Evaluation DTO round-trip PBT (PBT-02).
- [x] **Step 8 — Business Logic Summary**: `aidlc-docs/construction/evaluation-scoring/code/business-logic-summary.md`.
- [x] **Step 9 — Repository tests**: Testcontainers (deferred execution).
- [x] **Step 10 — Repository Summary**: `.../code/repository-summary.md`.
- [x] **Step 11 — API Layer**: `AssignmentController` (admin), `EvaluationController` (panel) + DTOs (AssignRequest, EvaluationRequest, EvaluationResponse, AggregateResponse, RankedIdeaResponse) with validation + authz guards.
- [x] **Step 12 — API tests**: controller/authz/validation (deferred execution).
- [x] **Step 13 — API Summary**: `.../code/api-summary.md`.
- [x] **Step 14 — Migration**: Flyway `V3__evaluation_scoring.sql` (assignments, evaluations tables + indexes + unique constraints).
- [x] **Step 15 — Deployment Summary**: `.../code/deployment-summary.md`.

## Story Traceability
| Step(s) | Stories |
|---|---|
| 2,3 | 6.1 (scoring/ranking math) |
| 6,11 | 2.1, 2.2, 2.3, 2.4, 3.3 |
| 3,7 | PBT coverage |

## Notes
- Scoring core (Step 2) has no framework dependency -> fast, pure PBT.
- Build/tests run in Build and Test (no toolchain here).
