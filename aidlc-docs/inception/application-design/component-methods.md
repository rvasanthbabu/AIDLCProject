# Component Methods — Ideation Portal

High-level method signatures per component. Detailed business rules, validation specifics, and algorithms are defined later in **Functional Design** (per-unit, Construction phase). Types are logical (language-neutral); concrete DTOs are defined in Functional Design.

Conventions: all mutating methods run within an authenticated, authorized context; `actor` denotes the authenticated principal; authorization is enforced server-side (NFR-S6).

---

## C1. Identity & Access

### AuthenticationService
- `authenticate(email, password) -> AuthResult{ token, user, mfaRequired }`
- `verifyMfa(userId, code) -> AuthResult`
- `validateToken(token) -> Principal`
- `logout(token) -> void`

### AuthorizationService
- `hasRole(principal, role) -> boolean`
- `requirePermission(principal, action, resourceRef) -> void` (throws if denied; object + function level)

### UserAccountService
- `createUser(actor, newUserData) -> User`
- `assignRole(actor, userId, role) -> User`
- `listUsers(actor, filter) -> Page<User>`

### AuthenticationProvider (abstraction — SSO-ready)
- `supports(providerType) -> boolean`
- `authenticate(credentials) -> Principal`

---

## C2. Idea Management

### IdeaService
- `createDraft(actor, ideaDraftData) -> Idea`
- `updateDraft(actor, ideaId, ideaDraftData) -> Idea`
- `getIdea(actor, ideaId) -> Idea` (ownership/role checked)
- `listMyIdeas(actor, cycleId?) -> List<IdeaSummary>`
- `submitIdea(actor, ideaId) -> Idea` (Draft -> Submitted; requires open cycle)

---

## C3. Cycle Management

### CycleService
- `createCycle(actor, cycleData) -> Cycle`
- `openCycle(actor, cycleId) -> Cycle`
- `closeCycle(actor, cycleId) -> Cycle` (emits CycleClosedEvent)
- `getActiveCycle() -> Cycle?`
- `isCycleOpen(cycleId) -> boolean`

---

## C4. Evaluation

### AssignmentService
- `assignIdea(actor, ideaId, panelMemberIds) -> List<Assignment>` (idea -> Under Evaluation)
- `removeAssignment(actor, assignmentId) -> void`
- `listAssignmentsForPanelMember(actor, cycleId) -> List<Assignment>`

### EvaluationService
- `getAssignedIdea(actor, ideaId) -> Idea` (only if assigned to actor)
- `submitEvaluation(actor, ideaId, scores{feasibility,impact,innovation}, comment?) -> Evaluation`
- `updateEvaluation(actor, evaluationId, scores, comment?) -> Evaluation` (allowed until cycle close)
- `getMyEvaluation(actor, ideaId) -> Evaluation?`

---

## C5. Scoring & Aggregation

### ScoringService (pure)
- `computeWeightedScore(scores{feasibility,impact,innovation}, weights) -> decimal`

### AggregationService
- `aggregateForIdea(ideaId) -> AggregateResult{ aggregateScore, perMetricAverages, evaluatorCount }` (computed on read)
- `rankIdeas(cycleId) -> List<RankedIdea>` (deterministic; tie-break by earliest submission)

---

## C6. Dashboard & Analytics (read/query)

### DashboardQueryService
- `getDashboard(actor, cycleId) -> DashboardView` (aggregated scores for all ideas)
- `getLeaderboard(actor, cycleId) -> List<RankedIdea>`
- `getComparativeView(actor, cycleId, ideaIds?) -> ComparativeView` (per-metric breakdowns)

### AnalyticsQueryService
- `getTopPerformers(actor, cycleId, limit) -> List<RankedIdea>`
- `getCriteriaComparison(actor, cycleId) -> CriteriaComparison`

---

## C7. Recognition

### RecognitionService
- `computeTopThree(cycleId) -> List<RankedIdea>` (uses AggregationService.rankIdeas)
- `awardRecognition(actor, cycleId) -> RecognitionResult` (badges, state transitions, emits RecognitionAwardedEvent)
- `getRecognition(cycleId) -> RecognitionResult`

---

## C8. Notification

### NotificationService
- `onIdeaAssigned(event: IdeaAssignedEvent) -> void` (sends email to panel member)
- `onRecognitionAwarded(event: RecognitionAwardedEvent) -> void` (emails submitter + management)

### EmailProvider (abstraction)
- `send(to, subject, body) -> DeliveryResult`

---

## C9. Administration

### AdminService (orchestration / delegation)
- `manageUser(actor, op, userData) -> User` (delegates to UserAccountService)
- `manageCycle(actor, op, cycleData) -> Cycle` (delegates to CycleService)
- `assign(actor, ideaId, panelMemberIds) -> List<Assignment>` (delegates to AssignmentService)
- `triggerRecognition(actor, cycleId) -> RecognitionResult` (delegates to RecognitionService)

### CriteriaConfigService
- `getWeights(cycleId?) -> Weights{feasibility,impact,innovation}`
- `setWeights(actor, weights) -> Weights` (stored as config, not hardcoded — NFR-M3)

---

## Internal Domain Events (Q7=B)
- `IdeaAssignedEvent{ ideaId, panelMemberIds, cycleId }` → consumed by Notification
- `CycleClosedEvent{ cycleId }` → consumed by Recognition (eligibility)
- `RecognitionAwardedEvent{ cycleId, recognizedIdeaIds }` → consumed by Notification

**Note**: All method-level business rules (exact validation bounds, weight normalization, aggregation formula, tie-break precedence details) are specified in Functional Design, where PBT-01 testable properties are also identified.
