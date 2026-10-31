# Business Logic Model — Unit U3: Evaluation & Scoring

Core scoring engine. Pure functions (score/aggregate/rank) are isolated for property-based testing.

## 1. Assignment (Story 3.3, admin-triggered via U1 Admin)
- `assignIdea(actorAdmin, ideaId, panelMemberIds)`:
  - Require idea state SUBMITTED or UNDER_EVALUATION; require cycle open.
  - Each panelMemberId must have PANEL_MEMBER role (checked via U1).
  - Create active Assignment per member (idempotent on unique (ideaId, panelMemberId)).
  - Transition idea SUBMITTED -> UNDER_EVALUATION via U2 `markUnderEvaluation` (first assignment).
  - Publish `IdeaAssignedEvent(ideaId, panelMemberIds, cycleId)` (consumed by Notification U4).
- `removeAssignment(actorAdmin, assignmentId)`:
  - Set active=false. Any existing Evaluation is KEPT and still counts (Q7=A).

## 2. Scoring (Stories 2.1, 2.2, 2.4)
- `listAssignedIdeas(actorPanel, cycleId)`: ideas actively assigned to this panel member (BR-U3-03).
- `submitEvaluation(actorPanel, ideaId, feasibility, impact, innovation, comment?)`:
  - Require an active assignment of this idea to the actor; require cycle open (BR-U3-04/05).
  - Validate each metric in 1..5 (BR-U3-06).
  - Compute `normalizedScore` from current global weights (see Scoring Functions).
  - Upsert the member's Evaluation (one per idea/member); set submittedAt on first, updatedAt on revise.
- `updateEvaluation(...)`: allowed only while cycle open (BR-U3-07).
- Independence (Q6=B): a panel member cannot read others' evaluations/scores for an idea before submitting their own; after submitting they may see aggregates (not individual raw scores). Enforced in read methods (BR-U3-08).

## 3. Scoring Functions (pure — PBT targets)
Given metrics f,i,n in [1,5] and weights wF,wI,wN (integers, wF+wI+wN=100):
- `weightedSum = f*wF + i*wI + n*wN`  (range: 100 when all=1, 500 when all=5)
- `normalizedScore = (weightedSum - 100) / 400 * 100`  (maps 100->0, 500->100; range [0,100]) (Q1=C)

Aggregation across evaluations E for an idea (Q2=A, Q3=B):
- `aggregateScore = mean(normalizedScore(e) for e in E)`  (null if E empty)
- `feasibilitySum = sum(e.feasibility)`, `impactSum`, `innovationSum`
- `evaluatorCount = |E|`

## 4. Scored Eligibility (Q5=B)
- An idea is eligible as SCORED once at least one evaluation exists. A transition hook (e.g., on first evaluation, or at cycle close) calls U2 `markScored` to move UNDER_EVALUATION -> SCORED. (Recognition at cycle close operates on SCORED ideas.)

## 5. Ranking (Story 6.1 input; used by U4)
- `rankIdeas(cycleId)`:
  - Consider ideas in the cycle with >=1 evaluation.
  - Order by aggregateScore DESC, then idea.submittedAt ASC (earliest first, FR-6.2), then ideaId ASC (stable secondary, Q4=A).
  - Assign 1-based ranks. Deterministic and total ordering.

## 6. Error Handling
- Fail closed: missing/inactive assignment -> FORBIDDEN/NOT_FOUND; out-of-range metric -> VALIDATION; closed cycle -> CONFLICT.
- Transactional writes; parameterized persistence.

## 7. Events
- Publishes `IdeaAssignedEvent` on assignment.

## 8. Testable Properties (PBT-01) — primary PBT unit
- **Normalized score bounds (PBT-03 invariant)**: for all f,i,n in [1,5] and valid weights, normalizedScore in [0,100]; equals 0 iff all=1; equals 100 iff all=5.
- **Weighted monotonicity**: increasing any metric (others fixed) never decreases normalizedScore.
- **Aggregate within bounds (PBT-03)**: aggregateScore in [min,max] of its inputs; equals the single value when one evaluation.
- **Ranking determinism & totality**: rankIdeas yields a deterministic strict order; tie-break earliest-submission then id; stable across runs.
- **Evaluation DTO round-trip (PBT-02)**: serialize/deserialize identity.
