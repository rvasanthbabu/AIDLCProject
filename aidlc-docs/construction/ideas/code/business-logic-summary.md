# Business Logic Summary — U2 Ideas

## Generated (backend/ideas)
- Domain: `Idea` (guarded transitions), `IdeaCategory`, `IdeaState`
- Repository: `IdeaRepository` (ownership-scoped, parameterized)
- Service: `IdeaService` (createDraft, updateDraft, submitIdea, listMyIdeas, getMyIdea, deleteDraft + cross-unit mutators markUnderEvaluation/markScored/markRecognized/markRejected), `IdeaDraftData`

## Rules implemented
BR-U2-01..12: role (API) + ownership (service) authz, field limits, cycle-open gating on submit (via U1 CycleService), post-submit lock, delete-while-draft, guarded transitions, transactional + parameterized, fail-closed. Submission audited via common-core AuditLogWriter.

## PBT (partial)
- `IdeaResponseRoundTripPropertyTest` — JSON serialize/deserialize identity (PBT-02) with jqwik generators + shrinking + seeds (PBT-07/08).
- Example-based: `IdeaStateTransitionTest`.

## Notes
- Build/tests execute in Build and Test (no JDK/Maven here). Manually reviewed for type/wiring consistency.
