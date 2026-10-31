# Services — Ideation Portal

Service-layer definitions and orchestration patterns. The backend is a modular monolith; each domain component exposes a service interface. A thin API layer (REST command controllers + read/query controllers) sits above the services. Side effects use internal domain events (Q7=B).

---

## Service Layering

```
+-------------------------------------------------------------+
|  API Layer (Spring MVC)                                     |
|  - Command Controllers (REST, mutating)                     |
|  - Query Controllers (read/query API for dashboards)        |
|  - Security filters: authN, authZ, validation, headers      |
+-------------------------------------------------------------+
|  Application / Service Layer (per component)                |
|  AuthenticationService  UserAccountService  IdeaService     |
|  CycleService  AssignmentService  EvaluationService         |
|  ScoringService  AggregationService  RecognitionService     |
|  CriteriaConfigService  NotificationService                 |
|  DashboardQueryService  AnalyticsQueryService  AdminService |
+-------------------------------------------------------------+
|  Domain Events (in-process publish/subscribe)               |
+-------------------------------------------------------------+
|  Persistence Layer (repositories) -> PostgreSQL             |
+-------------------------------------------------------------+
```

(ASCII diagram uses only + - | and spaces per ascii-diagram-standards.)

---

## Orchestration Patterns

### Command path (writes)
- REST command controllers validate input (NFR-S4), check authorization (NFR-S6), then invoke the owning component's service.
- Services perform their domain logic transactionally and, where a side effect is needed, publish a domain event rather than calling peer components directly for the side effect.

### Query path (reads)
- Dedicated query controllers call `DashboardQueryService` / `AnalyticsQueryService`.
- Aggregation is computed **on read** (Q4=B) by `AggregationService`, reading evaluations and criteria weights from PostgreSQL.
- Frontend polls query endpoints (~30-60s) for near real-time refresh (FR-4.5).

### Key orchestrations
1. **Submit idea**: `IdeaService.submitIdea` → validates open cycle via `CycleService.isCycleOpen` → sets state Submitted.
2. **Assign for evaluation**: `AdminService.assign` → `AssignmentService.assignIdea` → sets idea Under Evaluation → publishes `IdeaAssignedEvent` → `NotificationService` emails panel members.
3. **Submit evaluation**: `EvaluationService.submitEvaluation` → verifies assignment + cycle open → persists scores (independence enforced by not exposing peers' scores).
4. **Dashboard read**: `DashboardQueryService.getDashboard` → `AggregationService.aggregateForIdea` (per idea) + `ScoringService.computeWeightedScore`.
5. **Close cycle & recognize**: `CycleService.closeCycle` → publishes `CycleClosedEvent`; Administrator triggers `AdminService.triggerRecognition` → `RecognitionService.awardRecognition` → `AggregationService.rankIdeas` → sets Recognized/Rejected → publishes `RecognitionAwardedEvent` → `NotificationService` emails submitter + management.

---

## Transaction & Consistency
- Each command executes in a single DB transaction; domain events are published after commit (to avoid notifying on rolled-back changes).
- Notification delivery is best-effort and asynchronous relative to the command; failure to send email must not fail the originating command (graceful degradation, NFR-R5).

## Security Isolation
- `AuthenticationService` and `AuthorizationService` are the only components handling credentials/permissions (SECURITY-11 separation of concerns).
- Every controller applies an authorization guard; object-level checks live in the owning service (prevent IDOR — NFR-S6).

## Service Extraction Readiness (Q1=C)
- Components communicate only through their public service interfaces and domain events, so a future extraction of (for example) Notification or Analytics into a separate service requires replacing in-process event delivery with a broker/HTTP transport without changing domain logic.
