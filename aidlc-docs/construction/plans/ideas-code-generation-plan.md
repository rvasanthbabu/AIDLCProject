# Code Generation Plan — Unit U2: Ideas

**Single source of truth for U2 code generation.** Execute in order; mark [x] on completion.

## Unit Context
- **Unit**: U2 Ideas (Idea Management)
- **Stories**: 1.1 (draft), 1.2 (submit), 1.3 (view my ideas)
- **Dependencies**: U1 — needs `CycleService` (active/open cycle) and `CurrentUser`/authz. Reuses common-core (error handling, audit, logging).
- **Entities owned**: Idea (+ IdeaCategory, IdeaState enums)
- **Module**: new Maven module `backend/ideas/` added to parent pom; depends on common-core and platform-identity.
- **Code location**: `backend/ideas/src/main/java/com/ideationportal/idea/...`

## Target Structure
```
backend/ideas/
  pom.xml
  src/main/java/com/ideationportal/idea/
    domain/      (Idea, IdeaCategory, IdeaState)
    repository/  (IdeaRepository)
    service/     (IdeaService, IdeaLifecycle)
    api/         (IdeaController, DTOs)
  src/test/java/com/ideationportal/idea/
```

---

## Steps

- [x] **Step 1 — Module scaffolding**: Create `backend/ideas/pom.xml`; add `ideas` module to parent `backend/pom.xml` and to `app` dependencies so it is wired into the deployable.
- [x] **Step 2 — Domain**: `Idea` JPA entity, `IdeaCategory` enum, `IdeaState` enum per domain-entities.md (with guarded transition methods).
- [x] **Step 3 — Repository**: `IdeaRepository` (findByIdAndSubmitterId, paged findBySubmitterId, findByCycleIdAndState) — parameterized (SECURITY-05).
- [x] **Step 4 — Service**: `IdeaService` (createDraft, updateDraft, submitIdea, listMyIdeas, getIdea, deleteDraft) + cross-unit lifecycle mutators (markUnderEvaluation/markScored/markRecognized/markRejected). Enforce BR-U2-01..12; integrate U1 `CycleService` for open-cycle gating; audit submit.
- [x] **Step 5 — Service tests + PBT**: example-based (ownership, state transitions, submit gating) + PBT (jqwik) Idea DTO round-trip (PBT-02) and state-transition guard invariant.
- [x] **Step 6 — Business Logic Summary**: `aidlc-docs/construction/ideas/code/business-logic-summary.md`.
- [x] **Step 7 — Repository tests**: Testcontainers integration tests (deferred execution to Build & Test).
- [x] **Step 8 — Repository Summary**: `aidlc-docs/construction/ideas/code/repository-summary.md`.
- [x] **Step 9 — API Layer**: `IdeaController` + DTOs (Create/UpdateIdeaRequest, IdeaResponse, IdeaSummaryResponse) with Bean Validation; SUBMITTER `@PreAuthorize`; ownership enforced in service. Endpoints: POST /api/ideas (draft), PUT /api/ideas/{id} (update draft), POST /api/ideas/{id}/submit, GET /api/ideas/mine, GET /api/ideas/{id}, DELETE /api/ideas/{id}.
- [x] **Step 10 — API tests**: controller/authz/validation tests (deferred execution to Build & Test).
- [x] **Step 11 — API Summary**: `aidlc-docs/construction/ideas/code/api-summary.md`.
- [x] **Step 12 — Database migration**: Flyway `V2__ideas.sql` (ideas table + indexes submitter_id, (cycle_id,state); FKs to users/cycles).
- [x] **Step 13 — Deployment summary**: `aidlc-docs/construction/ideas/code/deployment-summary.md` (no new infra; module wired into existing image).

## Story Traceability
| Step(s) | Stories |
|---|---|
| 4, 9 | 1.1, 1.2, 1.3 |
| 5, 10 | test coverage incl. PBT round-trip |

## Notes
- No new deployment artifacts; U2 is added to the existing backend image.
- Build/tests run in the Build and Test stage (no toolchain in authoring env).
