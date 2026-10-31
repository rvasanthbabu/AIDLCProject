# Unit of Work Plan — Ideation Portal

**Role**: Software Architect
**Sources**: `requirements.md`, `stories.md`, `application-design/*`

This plan defines how the system is decomposed into units of work for development. Because the backend is a **modular monolith** (single deployable), "units" here are **logical modules** within one deployable application (not separate services), grouped for coherent per-unit design and code generation.

Please answer the embedded questions (fill in each `[Answer]:` tag), then I'll generate the unit artifacts.

---

## Part A: Clarifying Questions

### Question 1: Unit Granularity / Grouping
How should the 9 components be grouped into development units?

A) One unit per domain component (9 units) — maximum isolation, more overhead

B) Capability-based grouping into ~4 units: (1) Platform & Identity [Identity, Admin, Cycle], (2) Ideas [Idea Management], (3) Evaluation & Scoring [Evaluation, Scoring & Aggregation], (4) Insights & Recognition [Dashboard & Analytics, Recognition, Notification]

C) Two units: Backend (all server modules) + Frontend (React app)

D) Three units: Core Backend, Analytics/Recognition Backend, Frontend

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

### Question 2: Frontend as a Separate Unit
Should the React frontend be its own unit, or split across backend units?

A) Frontend is one dedicated unit (single React SPA covering all roles) — recommended

B) Frontend split per capability unit (each unit ships its own UI slice)

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 3: Code Organization (Greenfield monolith)
How should the repository/directory structure be organized?

A) Monorepo: `backend/` (Spring Boot, packages per module) + `frontend/` (React), each independently buildable, Docker per app — recommended

B) Backend modules under `src/{module}/` within one project + separate `frontend/`

C) Fully separate repositories per unit

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 4: Build Ordering / Dependencies
How should units be sequenced for development, given dependencies?

A) Dependency-ordered sequential (foundation units first: Platform & Identity → Ideas → Evaluation & Scoring → Insights & Recognition → Frontend)

B) Backend-first then Frontend, backend units in parallel where possible

C) No strict ordering preference — recommend the optimal sequence

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

### Question 5: Shared Code / Cross-Cutting
How should shared concerns (security filters, common DTOs, domain-event bus, persistence config) be handled?

A) A shared "common/core" module that other backend modules depend on — recommended

B) Duplicate per module (avoid shared coupling)

C) Keep cross-cutting in the app shell (single Spring Boot application context) with per-module packages

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Part B: Execution Checklist (runs after approval)

- [x] Generate `aidlc-docs/inception/application-design/unit-of-work.md` with unit definitions, responsibilities, and greenfield code organization strategy
- [x] Generate `aidlc-docs/inception/application-design/unit-of-work-dependency.md` with the unit dependency matrix and build order
- [x] Generate `aidlc-docs/inception/application-design/unit-of-work-story-map.md` mapping all 26 stories to units
- [x] Validate unit boundaries, dependencies, and that every story is assigned to exactly one primary unit
