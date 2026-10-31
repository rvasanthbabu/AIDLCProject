# Requirements Document — Ideation Portal

## 1. Intent Analysis Summary

- **User Request**: Build an "Ideation Portal" — a digital platform enabling employees to submit innovative ideas for transparent evaluation, scoring, and recognition, to foster a culture of innovation.
- **Request Type**: New Project (greenfield)
- **Scope Estimate**: System-wide (full-stack web application with multiple components)
- **Complexity Estimate**: Moderate-to-Complex
- **Project Type**: Greenfield
- **Enabled Extensions**: Security Baseline (full), Resiliency Baseline (full), Property-Based Testing (partial)

## 2. Business Context

- **Goal**: Foster organizational innovation by giving employees a transparent channel to submit ideas and have them fairly evaluated and recognized.
- **Primary Value**: Transparent, multi-dimensional, panel-based evaluation with recognition of top ideas.
- **Success Criteria**:
  - Employees can submit structured ideas (with draft saving).
  - Authorized panel members can independently and fairly score ideas across defined dimensions.
  - Aggregated scores are visible on dashboards/leaderboards in near real-time.
  - The top 3 ideas per cycle are recognized.
- **Stakeholders**: Employees (submitters), Evaluation Panel Members, Administrators, Leadership/Viewers.

## 3. User Roles

| Role | Description | Key Capabilities |
|---|---|---|
| Submitter (Employee) | Submits and manages their own ideas | Create/edit/save-draft/submit ideas; view own submissions and their status; view public dashboards |
| Panel Member (Evaluator) | Independently evaluates assigned ideas | View assigned ideas; score across feasibility, impact, innovation; submit/edit evaluations until cycle close |
| Administrator | Manages the platform | Manage users/roles; configure evaluation criteria & weights; create/open/close cycles; assign ideas to panel members; trigger recognition; manage reward notes |
| Viewer | Read-only visibility | View dashboards, leaderboards, and analytics (no submit/evaluate) |

## 4. Functional Requirements

### 4.1 Idea Submission (Feature 1)
- **FR-1.1**: Submitters can create an idea via a structured form with defined input fields (title, problem statement, proposed solution, expected impact, category, attachments/links — final field set confirmed in Functional Design).
- **FR-1.2**: Submitters can save an idea as a **draft** and return later to continue editing.
- **FR-1.3**: Submitters can submit a completed idea, transitioning it from Draft to Submitted.
- **FR-1.4**: Submitters can view a list of their own ideas and the current status of each.
- **FR-1.5**: Input fields enforce validation (required fields, max lengths, allowed formats) before submission.
- **FR-1.6**: Submissions are tied to the active cycle (see FR-5).

### 4.2 Evaluation Framework (Feature 2)
- **FR-2.1**: The system supports three evaluation metrics: **Feasibility**, **Impact**, **Innovation**.
- **FR-2.2**: Each metric is scored on a **1-5** scale by each panel member.
- **FR-2.3**: Each metric has a **configurable weight** (set by Administrator). The final per-evaluation score is the **weighted sum** of the three metric scores.
- **FR-2.4**: Panel members evaluate **independently** — a panel member cannot see other members' scores for an idea before submitting their own (independence requirement).
- **FR-2.5**: Panel members may add qualitative comments alongside scores (field set confirmed in Functional Design).
- **FR-2.6**: Evaluations can be edited by the panel member until the cycle closes.

### 4.3 Secure Role-Based Access & Evaluation Assignment (Feature 3)
- **FR-3.1**: Access is **role-based** (Submitter, Panel Member, Administrator, Viewer).
- **FR-3.2**: **Administrators assign specific ideas to specific panel members** for evaluation.
- **FR-3.3**: Panel members can only view and score ideas assigned to them.
- **FR-3.4**: Authorization is enforced server-side on every request (object-level and function-level).

### 4.4 Dashboards & Leaderboards (Feature 4)
- **FR-4.1**: Real-time (near real-time) dashboards display **aggregated panel scores** for all ideas.
- **FR-4.2**: Leaderboards rank ideas by aggregated weighted score.
- **FR-4.3**: Dashboards provide **multi-dimensional comparative views** (per-metric breakdowns: feasibility vs impact vs innovation).
- **FR-4.4**: Aggregated scores aggregate across all panel members who evaluated an idea (aggregation method — e.g., mean of weighted sums — finalized in Functional Design).
- **FR-4.5**: "Near real-time" is defined as refresh-on-page-load plus short polling (approximately every 30-60 seconds). Live push (websockets) is explicitly out of scope for v1.

### 4.5 Analytics (Feature 5)
- **FR-5.1**: Analytics highlight **top-performing ideas** within a cycle.
- **FR-5.2**: Analytics provide **comparative analysis across evaluation criteria** (e.g., which ideas score high on innovation but low on feasibility).
- **FR-5.3**: Analytics are scoped to cycles and support comparison within a cycle.

### 4.6 Recognition System (Feature 6)
- **FR-6.1**: The system identifies and recognizes the **three highest-scoring ideas** per cycle.
- **FR-6.2**: **Tie-breaking rule**: ties are broken by **earliest submission timestamp**.
- **FR-6.3**: Recognition grants a **digital badge** to the recognized idea/submitter and highlights it on the leaderboard.
- **FR-6.4**: Recognition triggers **notifications** to the submitter and to management.
- **FR-6.5**: Recognition is awarded **per cycle** (at or after cycle close).

### 4.7 Cycles / Campaigns (cross-cutting)
- **FR-7.1**: Evaluations run in **time-boxed cycles** with defined open and close dates.
- **FR-7.2**: Administrators create, open, and close cycles.
- **FR-7.3**: Submissions and evaluations are only accepted while a cycle is open.
- **FR-7.4**: Recognition is computed per cycle after close.

### 4.8 Idea Lifecycle (cross-cutting)
- **FR-8.1**: Idea states: **Draft → Submitted → Under Evaluation → Scored → Recognized / Rejected**.
- **FR-8.2**: State transitions are governed by role and cycle status (e.g., only Admin assignment moves an idea to Under Evaluation; cycle close moves Scored ideas to Recognized/Rejected).

### 4.9 Authentication & Account Management (cross-cutting)
- **FR-9.1**: Authentication uses **email/password** with application-managed accounts for v1.
- **FR-9.2**: The design must be **SSO-ready** (SAML/OIDC) so corporate SSO can be added later without re-architecture (pluggable auth abstraction).
- **FR-9.3**: Administrators manage users and role assignments.

### 4.10 Notifications (cross-cutting)
- **FR-10.1**: The system sends notifications for key events (idea assigned to panel member, recognition awarded). Delivery channel (in-app and/or email) finalized in Functional Design.

## 5. Non-Functional Requirements

### 5.1 Performance & Scale
- **NFR-P1**: Target scale is **enterprise-wide** — thousands of users with **high concurrency** (concurrent submissions during cycle windows and concurrent dashboard/evaluation access).
- **NFR-P2**: Dashboards refresh in near real-time (~30-60s polling); API read latency target to be set in NFR Requirements.
- **NFR-P3**: The system must scale horizontally (stateless application tier) to handle load spikes near cycle deadlines.

### 5.2 Availability & Resiliency (Resiliency Baseline)
- **NFR-R1 (RESILIENCY-02)**: Target availability **99.95%** (~4.4 hours downtime/year).
- **NFR-R2 (RESILIENCY-02/11)**: **RTO/RPO = within-region recovery**; DR strategy = **Backup & Restore within a single region**. No cross-region DR required (user decision R1=E).
- **NFR-R3 (RESILIENCY-08)**: **Single-region, multi-zone** deployment. Compute distributed across ≥2 availability zones; PostgreSQL in a multi-zone (primary + standby) configuration; load balancing across zones; architecture must remain statically stable under single-zone loss.
- **NFR-R4 (RESILIENCY-09)**: Auto-scaling for the application tier with defined min/max limits; service-quota awareness.
- **NFR-R5 (RESILIENCY-10)**: Explicit timeouts on all external calls (DB, cache, notification); circuit breakers / graceful degradation for non-critical dependencies (e.g., notifications).
- **NFR-R6 (RESILIENCY-12)**: Automated, encrypted PostgreSQL backups with a defined retention policy and periodic restore validation.
- **NFR-R7 (RESILIENCY-01)**: Workload criticality classification and dependency mapping documented in design.
- **NFR-R8 (RESILIENCY-03)**: Change management conforms to the organization's **existing change management process** (tool/name to be provided by the ops team; user decision R3=A).
- **NFR-R9 (RESILIENCY-04)**: Deployment is **automated via the organization's existing CI/CD pipeline** (tool to be provided; R4=A). Deployment style = **Canary** (R6=D) with **canary auto-rollback on health/metric regression** (R5=C).
- **NFR-R10 (RESILIENCY-05/06)**: Observability across metrics, logs, and traces; shallow + deep health checks; operational dashboards and alerting.
- **NFR-R11 (RESILIENCY-15)**: Alerting integrates with the organization's **existing incident response process** (reference to be provided; R7=A).
- **NFR-R12 (RESILIENCY-14)**: AI-DLC will **propose a DR testing / chaos experiment plan** (R9=B); execution deferred to Operations.

### 5.3 Security (Security Baseline — all rules blocking)
- **NFR-S1 (SECURITY-01)**: Encryption at rest and in transit for PostgreSQL and any object/file storage; TLS 1.2+ enforced.
- **NFR-S2 (SECURITY-03/14)**: Structured, centralized application logging with correlation IDs; no secrets/PII in logs; security event alerting; ≥90-day log retention; tamper-evident audit logs.
- **NFR-S3 (SECURITY-04)**: HTTP security headers (CSP, HSTS, X-Content-Type-Options, X-Frame-Options, Referrer-Policy) on all HTML-serving endpoints.
- **NFR-S4 (SECURITY-05)**: Input validation on all API parameters (type, length, format allowlists, sanitization, parameterized queries).
- **NFR-S5 (SECURITY-06)**: Least-privilege access policies for all service identities.
- **NFR-S6 (SECURITY-08)**: Deny-by-default application-level authorization; object-level (prevent IDOR) and function-level checks; restricted CORS; server-side token validation on every request. This directly enforces evaluator independence (FR-2.4) and assignment scoping (FR-3.3).
- **NFR-S7 (SECURITY-09/15)**: Hardening (no default credentials, generic production error messages, fail-closed error handling, global error handler).
- **NFR-S8 (SECURITY-10/13)**: Dependency pinning + lock files, vulnerability scanning in CI, pinned Docker base images (no `latest`), SBOM for production, SRI for external scripts.
- **NFR-S9 (SECURITY-11)**: Secure design — security-critical logic isolated in dedicated modules; rate limiting on public endpoints; abuse-case consideration.
- **NFR-S10 (SECURITY-12)**: Authentication hardening — password policy (min 8 chars, breached-password check), adaptive password hashing, MFA for admin accounts (available to all), secure session cookies (Secure/HttpOnly/SameSite), brute-force protection, no hardcoded credentials (use a secrets manager).

### 5.4 Testing (Property-Based Testing — Partial mode)
- **NFR-T1**: PBT is enforced in **Partial mode** — blocking rules are **PBT-02 (round-trip), PBT-03 (invariants), PBT-07 (generator quality), PBT-08 (shrinking/reproducibility), PBT-09 (framework selection)**; other PBT rules are advisory.
- **NFR-T2**: A PBT framework for Java (e.g., jqwik) must be selected and included in dependencies.
- **NFR-T3**: Target PBT properties include: scoring weighted-sum invariants (e.g., score bounds given weights and 1-5 inputs), aggregation invariants (aggregate within min/max of inputs), ranking/tie-break determinism, and serialization round-trips for idea/evaluation DTOs.
- **NFR-T4**: PBT complements example-based tests; business-critical paths retain explicit example-based tests.

### 5.5 Usability & Accessibility
- **NFR-U1**: Submission interface must be user-friendly with clear structured fields and draft-save feedback.
- **NFR-U2**: Front-end should follow accessibility best practices (WCAG-aligned); full WCAG validation requires separate manual testing.

### 5.6 Maintainability & Portability
- **NFR-M1**: Application is **containerized with Docker** (host-agnostic; deployable to the organization's container platform).
- **NFR-M2**: Clear separation between frontend (React), backend (Spring Boot), and data (PostgreSQL) tiers.
- **NFR-M3**: Configuration (criteria weights, cycle dates, metric definitions) is data-driven, not hardcoded.

## 6. Technical Context

- **Frontend**: React
- **Backend**: Java / Spring Boot (REST API)
- **Database**: PostgreSQL
- **Packaging**: Docker containers
- **Deployment target**: Single region, multi-zone; container platform (host to be confirmed in Infrastructure Design); no cross-region DR
- **Auth**: Email/password now, SSO-ready (SAML/OIDC) later
- **Data requirements**: Users/roles, ideas, cycles, evaluation criteria & weights, assignments, evaluations/scores, aggregated results, recognition records, audit log, notifications

## 7. Constraints & Assumptions

- **C-1**: Deployment is container-based; specific orchestration platform (e.g., Kubernetes vs ECS) to be decided in Infrastructure Design.
- **C-2**: Existing organizational processes (change management, CI/CD tooling, incident response) will be referenced by name during Construction once the ops team provides them; requirements proceed with "existing process (to be named)".
- **A-1**: One active evaluation cycle at a time is assumed for v1 unless Functional Design determines otherwise.
- **A-2**: Notification channel (in-app vs email vs both) to be finalized in Functional Design.
- **A-3**: Exact submission form field set and comment fields to be finalized in Functional Design.

## 8. Out of Scope (v1)

- Live websocket push updates (polling is used instead).
- Multi-region / cross-region disaster recovery.
- Corporate SSO integration (design is SSO-ready, but SSO itself is deferred).
- Monetary reward disbursement (recognition is badge + notification + admin reward notes only).

## 9. Key Requirements Summary

The Ideation Portal is an enterprise-scale, Dockerized React + Spring Boot + PostgreSQL web application with four roles (Submitter, Panel Member, Administrator, Viewer). Employees submit structured ideas (with drafts) into time-boxed cycles; administrators assign ideas to panel members who independently score feasibility/impact/innovation on a 1-5 scale with configurable weighted-sum scoring; near real-time dashboards and leaderboards present aggregated, multi-dimensional comparisons; analytics surface top performers; and the top 3 ideas per cycle (ties broken by earliest submission) are recognized with badges and notifications. The system is designed for 99.95% availability in a single-region multi-zone topology with Backup & Restore DR, canary deployments with auto-rollback, full security-baseline enforcement, and partial property-based testing.
