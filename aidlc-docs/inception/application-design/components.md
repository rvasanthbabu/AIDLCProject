# Components — Ideation Portal

**Architectural style**: Modular monolith (Spring Boot, single deployable), with clear module boundaries designed for future service extraction (decision Q1=C).
**API style**: REST for commands/mutations + a dedicated read/query API for dashboards and analytics (Q2=C).
**Internal communication**: In-process synchronous service calls plus an internal domain-event mechanism for side effects such as notifications and lifecycle transitions (Q7=B).

Each component is a backend module with a clear responsibility, a public service interface, and its own persistence entities. Security-critical logic (auth, authorization) is isolated in dedicated modules (SECURITY-11).

---

## C1. Identity & Access (identity)
- **Purpose**: Authentication, session/token management, user accounts, and role assignment. SSO-ready abstraction for later SAML/OIDC (FR-9.2).
- **Responsibilities**:
  - Email/password authentication with adaptive hashing, brute-force protection, MFA for admins (NFR-S10).
  - Issue/validate session tokens server-side on every request (NFR-S6).
  - Enforce role-based authorization (Submitter, Panel Member, Administrator, Viewer).
  - Pluggable authentication provider interface (local now; SSO later).
- **Interface**: `AuthenticationService`, `AuthorizationService`, `UserAccountService`.
- **Traces**: FR-9.1, FR-9.2, FR-9.3, FR-3.1, FR-3.4, Stories 3.1, 3.2, 8.7.

## C2. Idea Management (idea)
- **Purpose**: Create, draft, edit, submit, and retrieve ideas owned by submitters.
- **Responsibilities**:
  - Draft save/restore and field validation (FR-1.1, FR-1.2, FR-1.5).
  - Submit transition Draft→Submitted within an open cycle (FR-1.3, FR-7.3).
  - Enforce object-level ownership (submitters see only their ideas) (NFR-S6).
  - Hold idea lifecycle state (FR-8.1).
- **Interface**: `IdeaService`.
- **Traces**: FR-1.*, FR-8.1, Stories 1.1-1.3.

## C3. Cycle Management (cycle)
- **Purpose**: Manage time-boxed evaluation cycles/campaigns.
- **Responsibilities**:
  - Create/open/close cycles with open/close dates (FR-7.1, FR-7.2).
  - Expose current open cycle; enforce cycle-open gating for submission/evaluation (FR-7.3).
  - Emit cycle-closed event to trigger recognition eligibility (FR-7.4).
- **Interface**: `CycleService`.
- **Traces**: FR-7.*, Story 7.3.

## C4. Evaluation (evaluation)
- **Purpose**: Manage panel assignments and independent evaluations.
- **Responsibilities**:
  - Admin assigns ideas to panel members; move idea to Under Evaluation (FR-3.2, FR-8.2).
  - Panel members view only assigned ideas (FR-3.3, NFR-S6).
  - Record per-member scores (feasibility/impact/innovation, 1-5) and comments (FR-2.1, FR-2.2, FR-2.5).
  - Enforce evaluator independence — hide others' scores pre-submission (FR-2.4).
  - Allow edits until cycle close (FR-2.6).
- **Interface**: `AssignmentService`, `EvaluationService`.
- **Traces**: FR-2.*, FR-3.2, FR-3.3, Stories 2.1-2.4, 3.3.

## C5. Scoring & Aggregation (scoring)
- **Purpose**: Compute per-evaluation weighted-sum scores and aggregate across panel members.
- **Responsibilities**:
  - Compute weighted sum from metric scores and configured weights (FR-2.3).
  - Aggregate scores across evaluations per idea on read (decision Q4=B).
  - Provide deterministic ranking with tie-break by earliest submission (FR-4.2, FR-6.2).
  - Pure, well-defined functions — primary target for PBT (NFR-T3).
- **Interface**: `ScoringService`, `AggregationService`.
- **Traces**: FR-2.3, FR-4.2, FR-4.4, FR-6.1, FR-6.2, NFR-T3.

## C6. Dashboard & Analytics (read/query)
- **Purpose**: Read-optimized query API for dashboards, leaderboards, and analytics.
- **Responsibilities**:
  - Serve aggregated scores and leaderboards (near real-time via polling) (FR-4.1, FR-4.2, FR-4.5).
  - Multi-dimensional comparative views (FR-4.3).
  - Top performers and cross-criteria analytics (FR-5.1, FR-5.2), computed on read; analytics engine choice deferred to NFR/Infra Design (Q5=C).
  - Read-only; enforces role-based read access.
- **Interface**: `DashboardQueryService`, `AnalyticsQueryService`.
- **Traces**: FR-4.*, FR-5.*, Stories 4.1-4.3, 5.1-5.2.

## C7. Recognition (recognition)
- **Purpose**: Determine and award recognition for the top 3 ideas per cycle.
- **Responsibilities**:
  - On cycle close, compute top 3 via Scoring/Aggregation with tie-break (FR-6.1, FR-6.2).
  - Award digital badge, highlight on leaderboard, set idea state Recognized/Rejected (FR-6.3, FR-8.1).
  - Emit recognition event for notifications (FR-6.4).
- **Interface**: `RecognitionService`.
- **Traces**: FR-6.*, Stories 6.1-6.2.

## C8. Notification (notification)
- **Purpose**: Deliver email notifications for key events (v1: email only, Q6=B).
- **Responsibilities**:
  - Consume internal domain events (idea assigned, recognition awarded) (FR-10.1).
  - Send email via a provider abstraction; degrade gracefully if provider is unavailable (NFR-R5).
- **Interface**: `NotificationService`, `EmailProvider` (abstraction).
- **Traces**: FR-10.1, FR-3.2 (assignment), FR-6.4 (recognition), Story 8.5 (graceful degradation).

## C9. Administration (admin)
- **Purpose**: Administrative configuration surface (thin orchestration over other components).
- **Responsibilities** (scope per Q7=A in User Stories):
  - User/role management (delegates to Identity & Access) (FR-9.3).
  - Evaluation criteria & weight configuration, stored as data (FR-2.3, NFR-M3).
  - Cycle management (delegates to Cycle) (FR-7.2).
  - Idea-to-panel assignment (delegates to Evaluation) (FR-3.2).
  - Trigger recognition (delegates to Recognition) (FR-6.5).
- **Interface**: `AdminService`, `CriteriaConfigService`.
- **Traces**: FR-9.3, FR-2.3, FR-7.2, FR-3.2, FR-6.5, Stories 7.1-7.3.

---

## Cross-Cutting Concerns (not domain components)
- **Security**: encryption, headers, input validation, authz filters (Security Baseline) — applied as framework filters/middleware across all components.
- **Observability**: structured logging, metrics, tracing, health checks (Resiliency Baseline) — cross-cutting infrastructure.
- **Persistence**: PostgreSQL via a repository layer per component.
