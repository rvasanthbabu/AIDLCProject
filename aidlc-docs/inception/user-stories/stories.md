# User Stories — Ideation Portal

**Organization**: Epic-based (one epic per feature area + cross-cutting technical epic)
**Acceptance Criteria Format**: Given/When/Then
**Granularity**: Medium (one story per distinct user action)
**Prioritization**: P0 (must-have for v1), P1 (important), P2 (nice-to-have)
**NFR Coverage**: Key NFRs woven into acceptance criteria + dedicated cross-cutting technical stories (Epic 7)
**Traceability**: Each story references requirements (FR/NFR) from `requirements.md`

INVEST note: stories are written to be Independent, Negotiable, Valuable, Estimable, Small, and Testable.

---

## Epic 1: Idea Submission

### Story 1.1 — Create and save an idea draft (P0)
**Persona**: Priya (Submitter) | **Traces**: FR-1.1, FR-1.2, FR-1.5
As a Submitter, I want to create and save a draft idea so that I can complete my submission over time without losing work.

- **Given** I am an authenticated Submitter and a cycle is open, **When** I open the new idea form and enter partial content, **Then** I can save it as a draft tied to the current cycle.
- **Given** I have a saved draft, **When** I reopen it, **Then** my previously entered content is restored.
- **Given** I am entering field values, **When** a field violates validation (required/max-length/format), **Then** I see a clear validation message and cannot save invalid data silently.

### Story 1.2 — Submit a completed idea (P0)
**Persona**: Priya (Submitter) | **Traces**: FR-1.3, FR-1.5, FR-7.3, FR-8.1
As a Submitter, I want to submit a completed idea so that it enters the evaluation process.

- **Given** I have a draft with all required fields valid and the cycle is open, **When** I submit, **Then** the idea transitions from Draft to Submitted and is associated with the active cycle.
- **Given** the cycle is closed, **When** I attempt to submit, **Then** submission is rejected with an explanatory message.
- **Given** I submit successfully, **When** I view my ideas, **Then** the idea shows status "Submitted".

### Story 1.3 — View my ideas and their status (P0)
**Persona**: Priya (Submitter) | **Traces**: FR-1.4, FR-8.1
As a Submitter, I want to see all my ideas and their current status so that I can track progress.

- **Given** I have submitted ideas, **When** I open "My Ideas", **Then** I see each idea with its lifecycle state (Draft/Submitted/Under Evaluation/Scored/Recognized/Rejected).
- **Given** another user's idea exists, **When** I view "My Ideas", **Then** I only see ideas I own (object-level authorization — NFR-S6).

---

## Epic 2: Evaluation Framework

### Story 2.1 — View ideas assigned to me (P0)
**Persona**: Raj (Panel Member) | **Traces**: FR-3.2, FR-3.3, NFR-S6
As a Panel Member, I want to see only the ideas assigned to me so that I know what I must evaluate.

- **Given** an Administrator has assigned ideas to me in the open cycle, **When** I open my evaluation queue, **Then** I see exactly those assigned ideas.
- **Given** an idea is not assigned to me, **When** I try to access it directly by ID, **Then** access is denied (prevent IDOR — NFR-S6).

### Story 2.2 — Score an idea across three metrics (P0)
**Persona**: Raj (Panel Member) | **Traces**: FR-2.1, FR-2.2, FR-2.3, FR-2.5
As a Panel Member, I want to score an assigned idea on feasibility, impact, and innovation so that it can be fairly evaluated.

- **Given** I am evaluating an assigned idea, **When** I enter a score of 1-5 for each of feasibility, impact, and innovation, **Then** the system records my scores and computes the weighted-sum evaluation score using the configured weights.
- **Given** I enter a value outside 1-5, **When** I attempt to save, **Then** the input is rejected with a validation message (NFR-S4).
- **Given** I add a comment, **When** I save, **Then** my comment is stored with my evaluation.

### Story 2.3 — Evaluate independently of other panelists (P0)
**Persona**: Raj (Panel Member) | **Traces**: FR-2.4, NFR-S6
As a Panel Member, I want my evaluation to be independent so that scoring is unbiased.

- **Given** other panel members may have scored the same idea, **When** I view the idea before submitting my own evaluation, **Then** I cannot see other members' scores or comments.
- **Given** I have submitted my evaluation, **When** I view aggregate results, **Then** I may see aggregated outcomes per the dashboard rules but not individual raw scores of others (unless authorized).

### Story 2.4 — Revise my evaluation before cycle close (P1)
**Persona**: Raj (Panel Member) | **Traces**: FR-2.6, FR-7.3
As a Panel Member, I want to edit my scores until the cycle closes so that I can correct or refine them.

- **Given** the cycle is open and I have an existing evaluation, **When** I edit my scores or comment, **Then** the changes are saved and the weighted score recomputed.
- **Given** the cycle is closed, **When** I attempt to edit, **Then** editing is blocked with an explanatory message.

---

## Epic 3: Secure Access & Evaluation Assignment

### Story 3.1 — Authenticate with email/password (P0)
**Persona**: All roles | **Traces**: FR-9.1, NFR-S10
As a user, I want to sign in securely so that my access is protected and role-appropriate.

- **Given** I have valid credentials, **When** I sign in, **Then** I receive an authenticated session with a secure cookie (Secure/HttpOnly/SameSite — NFR-S10).
- **Given** I enter wrong credentials repeatedly, **When** I exceed the threshold, **Then** brute-force protection (lockout/delay/CAPTCHA) is applied (NFR-S10).
- **Given** I am an admin, **When** I sign in, **Then** MFA is required (NFR-S10).

### Story 3.2 — Enforce role-based authorization (P0)
**Persona**: All roles | **Traces**: FR-3.1, FR-3.4, NFR-S6
As the system, I want to enforce role-based access so that users can only perform actions permitted to their role.

- **Given** I am a Viewer, **When** I attempt to submit or score, **Then** the action is denied server-side.
- **Given** I am a Submitter, **When** I attempt an admin action, **Then** the action is denied server-side (function-level authorization — NFR-S6).
- **Given** any request, **When** it is processed, **Then** the session/token is validated server-side (NFR-S6).

### Story 3.3 — Administrator assigns ideas to panel members (P0)
**Persona**: Dana (Administrator) | **Traces**: FR-3.2, FR-8.2
As an Administrator, I want to assign submitted ideas to specific panel members so that evaluation coverage is controlled.

- **Given** an idea is Submitted in the open cycle, **When** I assign it to one or more panel members, **Then** those members see it in their queue and the idea moves to "Under Evaluation".
- **Given** I reassign or remove an assignment, **When** I save, **Then** the affected panel members' queues update accordingly.

---

## Epic 4: Dashboards & Leaderboards

### Story 4.1 — View aggregated scores on a dashboard (P0)
**Persona**: Victor (Viewer), Priya, Dana | **Traces**: FR-4.1, FR-4.4, NFR-P2
As a Viewer, I want to see aggregated panel scores for ideas so that I can understand how ideas are performing.

- **Given** ideas have been scored in the cycle, **When** I open the dashboard, **Then** I see each idea's aggregated score computed from all panel evaluations.
- **Given** new scores are recorded, **When** the dashboard polls (~30-60s) or I reload, **Then** aggregated values refresh in near real-time (FR-4.5, NFR-P2).

### Story 4.2 — View leaderboard ranking (P0)
**Persona**: Victor (Viewer), Dana | **Traces**: FR-4.2
As a Viewer, I want a leaderboard ranking ideas by aggregated score so that I can see top performers at a glance.

- **Given** scored ideas exist, **When** I open the leaderboard, **Then** ideas are ranked by aggregated weighted score in descending order.
- **Given** two ideas have equal scores, **When** they are ranked, **Then** ordering is deterministic (earliest submission first, consistent with recognition tie-break FR-6.2).

### Story 4.3 — Compare ideas across evaluation dimensions (P1)
**Persona**: Victor (Viewer), Dana | **Traces**: FR-4.3
As a Viewer, I want multi-dimensional comparative views so that I can compare ideas on feasibility, impact, and innovation separately.

- **Given** scored ideas exist, **When** I open the comparative view, **Then** I can see per-metric breakdowns (feasibility vs impact vs innovation) for ideas.
- **Given** I select specific ideas, **When** I compare them, **Then** the view shows their metric-by-metric differences.

---

## Epic 5: Analytics

### Story 5.1 — Highlight top-performing ideas (P1)
**Persona**: Victor (Viewer), Dana | **Traces**: FR-5.1, FR-5.3
As a Viewer, I want analytics highlighting top-performing ideas in a cycle so that standout ideas are easy to find.

- **Given** a cycle with scored ideas, **When** I open analytics, **Then** the top-performing ideas for that cycle are surfaced.

### Story 5.2 — Compare performance across criteria (P1)
**Persona**: Victor (Viewer), Dana | **Traces**: FR-5.2
As a Viewer, I want comparative analysis across evaluation criteria so that I can spot patterns (e.g., high innovation but low feasibility).

- **Given** scored ideas in a cycle, **When** I run comparative analysis, **Then** I can see how ideas rank differently across each criterion.

---

## Epic 6: Recognition

### Story 6.1 — Identify the top 3 ideas with tie-breaking (P0)
**Persona**: Dana (Administrator) | **Traces**: FR-6.1, FR-6.2, FR-7.4
As an Administrator, I want the system to determine the top 3 ideas per cycle so that recognition is objective and consistent.

- **Given** a cycle has closed with scored ideas, **When** recognition is computed, **Then** the three highest aggregated scores are selected.
- **Given** a tie in aggregated score affects the top 3, **When** ranking is resolved, **Then** the earlier-submitted idea ranks higher (FR-6.2).

### Story 6.2 — Award recognition and notify (P0)
**Persona**: Dana (Administrator), Priya (recipient) | **Traces**: FR-6.3, FR-6.4, FR-6.5, FR-8.1
As an Administrator, I want to award recognition to the top 3 so that innovators are formally acknowledged.

- **Given** the top 3 are determined after cycle close, **When** I trigger recognition, **Then** each recognized idea receives a digital badge and is highlighted on the leaderboard, and its state becomes "Recognized" (others become "Rejected" per FR-8.1).
- **Given** recognition is awarded, **When** it completes, **Then** the submitter and management receive notifications (FR-6.4, FR-10.1).

---

## Epic 7: Administration (configuration & cycles)

### Story 7.1 — Manage users and roles (P0)
**Persona**: Dana (Administrator) | **Traces**: FR-9.3, FR-3.1
As an Administrator, I want to manage users and assign roles so that access is correct.

- **Given** I am an Administrator, **When** I create or update a user and assign a role, **Then** that user gains the permissions for their role on next sign-in.

### Story 7.2 — Configure evaluation criteria and weights (P0)
**Persona**: Dana (Administrator) | **Traces**: FR-2.3, NFR-M3
As an Administrator, I want to configure metric weights so that scoring reflects program priorities.

- **Given** I am an Administrator, **When** I set weights for feasibility, impact, and innovation, **Then** subsequent weighted-sum calculations use those weights.
- **Given** weights are stored as configuration (not hardcoded), **When** I change them, **Then** no code change is required (NFR-M3).

### Story 7.3 — Create, open, and close cycles (P0)
**Persona**: Dana (Administrator) | **Traces**: FR-7.1, FR-7.2, FR-7.3
As an Administrator, I want to manage time-boxed cycles so that submission and evaluation happen within defined windows.

- **Given** I am an Administrator, **When** I create a cycle with open/close dates and open it, **Then** submissions and evaluations are accepted only while it is open.
- **Given** a cycle reaches its close, **When** it closes, **Then** submissions and evaluation edits are blocked and Scored ideas become eligible for recognition.

---

## Epic 8: Cross-Cutting Technical Stories (NFR)

### Story 8.1 — Secure data and transport (P0)
**Traces**: NFR-S1, NFR-M1
As the system owner, I want data encrypted at rest and in transit so that sensitive data is protected.

- **Given** PostgreSQL and any object storage, **When** data is stored or transmitted, **Then** encryption at rest is enabled and TLS 1.2+ is enforced.

### Story 8.2 — Input validation and injection prevention (P0)
**Traces**: NFR-S4
As the system owner, I want all API inputs validated so that malformed or malicious input is rejected.

- **Given** any API endpoint, **When** a request arrives, **Then** inputs are type/length/format validated and all DB access uses parameterized queries.

### Story 8.3 — HTTP security headers and hardening (P0)
**Traces**: NFR-S3, NFR-S7
As the system owner, I want security headers and safe error handling so that the web app resists common attacks.

- **Given** an HTML-serving response, **When** it is returned, **Then** CSP, HSTS, X-Content-Type-Options, X-Frame-Options, and Referrer-Policy headers are set.
- **Given** a production error occurs, **When** a response is returned to the user, **Then** it is generic (no stack traces) and the system fails closed (NFR-S7).

### Story 8.4 — Observability, logging, and alerting (P0)
**Traces**: NFR-S2, NFR-R10
As an operator, I want structured logs, metrics, traces, health checks, and alerts so that I can run the system reliably.

- **Given** any service, **When** it handles a request, **Then** it emits structured logs with correlation IDs (no secrets/PII) to a centralized service.
- **Given** the services run, **When** monitored, **Then** metrics/traces, shallow+deep health checks, dashboards, and security/operational alerts are available (alerts route to the existing incident-response process — NFR-R11).

### Story 8.5 — High availability and resilient dependencies (P0)
**Traces**: NFR-R1, NFR-R3, NFR-R4, NFR-R5
As the system owner, I want multi-zone HA and resilient dependency handling so that the platform meets 99.95% availability.

- **Given** production deployment, **When** provisioned, **Then** compute spans ≥2 availability zones, PostgreSQL uses a multi-zone configuration, and the app tier auto-scales within min/max limits.
- **Given** a downstream dependency (DB, notifications) is slow or unavailable, **When** a call is made, **Then** explicit timeouts apply and non-critical features (e.g., notifications) degrade gracefully (NFR-R5).

### Story 8.6 — Backups and recovery (P1)
**Traces**: NFR-R2, NFR-R6
As the system owner, I want automated encrypted backups so that data can be restored within the recovery objective.

- **Given** PostgreSQL, **When** operating, **Then** automated encrypted backups run on a schedule with a defined retention and periodic restore validation (Backup & Restore DR within region).

### Story 8.7 — SSO-ready authentication abstraction (P1)
**Traces**: NFR-S10, FR-9.2
As the system owner, I want a pluggable authentication layer so that corporate SSO can be added later without re-architecture.

- **Given** email/password auth in v1, **When** SSO (SAML/OIDC) is introduced later, **Then** it integrates through the existing auth abstraction without reworking authorization logic.

### Story 8.8 — Secure CI/CD with canary and auto-rollback (P1)
**Traces**: NFR-R9, NFR-S8
As the system owner, I want canary deployments with auto-rollback and supply-chain checks so that releases are safe.

- **Given** a release via the existing CI/CD pipeline, **When** deployed, **Then** it rolls out as a canary and auto-rolls-back on health/metric regression.
- **Given** the build, **When** it runs, **Then** dependencies are pinned (lock file), vulnerability scanning runs, Docker base images are pinned (no `latest`), and an SBOM is produced (NFR-S8).

---

## Persona-to-Story Map

| Epic / Story | Priya (Submitter) | Raj (Panel) | Dana (Admin) | Victor (Viewer) |
|---|---|---|---|---|
| 1.1 Draft idea | ✅ | | | |
| 1.2 Submit idea | ✅ | | | |
| 1.3 View my ideas | ✅ | | | |
| 2.1 View assigned | | ✅ | | |
| 2.2 Score idea | | ✅ | | |
| 2.3 Independent eval | | ✅ | | |
| 2.4 Revise eval | | ✅ | | |
| 3.1 Authenticate | ✅ | ✅ | ✅ | ✅ |
| 3.2 Role authz | ✅ | ✅ | ✅ | ✅ |
| 3.3 Assign ideas | | | ✅ | |
| 4.1 Dashboard | ✅ | | ✅ | ✅ |
| 4.2 Leaderboard | | | ✅ | ✅ |
| 4.3 Compare dimensions | | | ✅ | ✅ |
| 5.1 Top performers | | | ✅ | ✅ |
| 5.2 Criteria analysis | | | ✅ | ✅ |
| 6.1 Determine top 3 | | | ✅ | |
| 6.2 Award & notify | ✅ (recipient) | | ✅ | |
| 7.1 Manage users | | | ✅ | |
| 7.2 Configure weights | | | ✅ | |
| 7.3 Manage cycles | | | ✅ | |
| 8.x Technical (NFR) | (benefit) | (benefit) | (benefit) | (benefit) |

---

## Coverage Summary

- **Epics**: 8 (6 feature + 1 admin + 1 cross-cutting technical)
- **Stories**: 26 total
- **Priorities**: P0 = 18, P1 = 8, P2 = 0
- **Requirements traceability**: every functional requirement (FR-1..FR-10) and key NFRs (security, resiliency, performance, maintainability) are referenced by at least one story.
