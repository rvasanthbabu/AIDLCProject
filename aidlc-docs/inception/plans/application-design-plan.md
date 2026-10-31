# Application Design Plan — Ideation Portal

**Role**: Software Architect
**Sources**: `requirements.md`, `stories.md`, `personas.md`

This plan defines the approach for high-level component identification and service-layer design. Please answer the embedded questions (fill in each `[Answer]:` tag), then I'll generate the design artifacts.

---

## Part A: Clarifying Questions

### Question 1: Backend Architectural Style
What architectural style should the Spring Boot backend use?

A) Modular monolith — single deployable Spring Boot app with clear internal module boundaries (recommended for faster start, simpler ops; aligns with Docker single-image)

B) Microservices — separate deployable services per domain (higher operational complexity)

C) Modular monolith now, with module boundaries designed so services can be extracted later

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

### Question 2: API Style
What API style should the backend expose to the React frontend?

A) REST (JSON over HTTP) — conventional, cache-friendly

B) GraphQL — flexible querying for dashboards

C) REST for commands + a dedicated read/query API for dashboards

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

### Question 3: Component Boundaries (domains)
Do these functional components/domains match your intent?

A) Yes — Identity & Access, Idea Management, Cycle Management, Evaluation, Scoring & Aggregation, Dashboard & Analytics (read), Recognition, Notification, Administration

B) Mostly, but merge some (describe which)

C) Add/remove components (describe)

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 4: Score Aggregation Timing
When should aggregated scores for dashboards be computed?

A) On write — recompute and store an aggregate each time an evaluation is saved (fast reads, simple polling)

B) On read — compute aggregates on demand when the dashboard is requested

C) Hybrid — maintain a stored aggregate updated on write, served to dashboards via polling

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

### Question 5: Analytics Approach
How should analytics (top performers, cross-criteria comparison) be served?

A) Computed from the same PostgreSQL data via query endpoints (no separate analytics store) — recommended for this scale/scope

B) A separate read-optimized store / materialized views

C) Defer analytics engine choice to NFR/Infrastructure Design

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

### Question 6: Notification Delivery
How should notifications (assignment, recognition) be delivered in v1?

A) In-app notifications only (stored, shown in UI)

B) Email only

C) Both in-app and email

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

### Question 7: Inter-Component Communication
How should components communicate internally (within the backend)?

A) Direct in-process service calls (synchronous) — simplest for a modular monolith

B) In-process calls plus an internal event/async mechanism for side effects (e.g., notifications, aggregation)

C) Message broker (e.g., Kafka/RabbitMQ) between components

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## Part B: Execution Checklist (runs after approval)

- [x] Generate `components.md` — component definitions, responsibilities, interfaces
- [x] Generate `component-methods.md` — method signatures with input/output types (business rules deferred to Functional Design)
- [x] Generate `services.md` — service definitions, responsibilities, orchestration patterns
- [x] Generate `component-dependency.md` — dependency matrix, communication patterns, data flow
- [x] Generate consolidated `application-design.md`
- [x] Validate design completeness, consistency, and traceability to requirements/stories
