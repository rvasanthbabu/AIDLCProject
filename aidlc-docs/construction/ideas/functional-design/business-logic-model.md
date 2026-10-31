# Business Logic Model — Unit U2: Ideas

Technology-agnostic business logic for drafting, submitting, and viewing ideas. Depends on U1 for authorization (SUBMIT_IDEA requires SUBMITTER role) and cycle gating (`isCycleOpen` / `getActiveCycle`).

## 1. Create / Save Draft (Story 1.1)
- `createDraft(actor, data)`:
  - Require actor role SUBMITTER (BR-U2-01).
  - Create Idea in state DRAFT, owner = actor, cycleId = null (drafts cycle-independent, Q5=B).
  - Partial content allowed; apply max-length checks on whatever fields are provided.
- `updateDraft(actor, ideaId, data)`:
  - Require ownership (actor == submitter) (BR-U2-02, prevent IDOR).
  - Require current state DRAFT (BR-U2-05); otherwise reject.
  - Save updated fields.

## 2. Submit Idea (Story 1.2)
- `submitIdea(actor, ideaId)`:
  - Require ownership + state DRAFT.
  - Require an open cycle via U1 `getActiveCycle()`; if none open, reject (BR-U2-06).
  - Validate all required fields present and within limits (BR-U2-04).
  - Bind cycleId to the active cycle; transition DRAFT -> SUBMITTED; set submittedAt.
- Idea is locked after submission (Q4=A): no further submitter edits (BR-U2-07).

## 3. View My Ideas (Story 1.3)
- `listMyIdeas(actor, cycleId?)`:
  - Return only ideas owned by actor (object-level authorization) (BR-U2-03).
  - Optionally filter by cycle; include current state.
- `getIdea(actor, ideaId)`:
  - Return only if actor owns it (submitter view). (Panel/admin access to ideas is handled in U3/U4 with their own authorization.)

## 4. Delete Draft (Story 1.1 / Q6=A)
- `deleteDraft(actor, ideaId)`:
  - Require ownership + state DRAFT; hard delete. No delete/withdraw after submission.

## 5. Cross-Unit Mutators (called by U3/U4 via service boundary)
- `markUnderEvaluation(ideaId)` — SUBMITTED -> UNDER_EVALUATION (invoked by U3 on assignment).
- `markScored(ideaId)` — UNDER_EVALUATION -> SCORED.
- `markRecognized(ideaId)` / `markRejected(ideaId)` — SCORED -> RECOGNIZED | REJECTED (invoked by U4).
- Each enforces the legal source state and is idempotent where safe.

## 6. Error Handling
- Fail closed: ownership/state violations return FORBIDDEN/CONFLICT with generic messages.
- All DB access parameterized; transactional writes.

## 7. Events
- U2 publishes no domain events in v1. (Assignment/recognition events originate in U3/U4.)

## 8. Testable Properties (PBT-01)
- **State-transition guard invariant**: only legal transitions succeed; illegal transitions always rejected (invariant — partial-mode advisory, documented).
- **Idea DTO serialization round-trip**: serialize(deserialize(x)) == x for the API DTO (PBT-02 round-trip — in scope for partial mode).
- **Ownership invariant**: listMyIdeas(actor) returns only ideas whose submitter == actor (invariant).
