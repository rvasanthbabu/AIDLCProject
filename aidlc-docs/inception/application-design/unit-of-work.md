# Units of Work — Ideation Portal

**Decomposition**: 4 capability backend modules + 1 frontend unit (5 units total), on a shared `common/core` module. The backend is a single deployable modular monolith; backend "units" are Maven/Gradle modules (logical modules) within `backend/`. The frontend is a separate React app. (Decisions Q1=B, Q2=A, Q3=A, Q5=A.)

---

## Unit List

### U0. Common/Core (shared module — not a feature unit)
- **Type**: Shared backend module (dependency of all backend units)
- **Responsibilities**: Cross-cutting concerns — security filters (authN/authZ, headers, input-validation utilities), error handling (global handler, fail-closed), shared DTO/base types, domain-event bus (in-process publish/subscribe), persistence/transaction configuration, logging/observability setup.
- **Maps to components**: Cross-cutting (Security, Observability, Persistence config), the event mechanism used by all.
- **Note**: Built first; every backend unit depends on it.

### U1. Platform & Identity (backend)
- **Components**: Identity & Access, Administration, Cycle Management
- **Responsibilities**: Authentication, authorization, user/role management, SSO-ready auth abstraction, administrative configuration (criteria & weights), and time-boxed cycle lifecycle (open/close gating + CycleClosedEvent).
- **Why grouped**: These are the foundational platform capabilities every other unit relies on (identity for authz, cycles for gating, admin for configuration).

### U2. Ideas (backend)
- **Components**: Idea Management
- **Responsibilities**: Idea drafts, submission, ownership enforcement, and lifecycle state up to Submitted.
- **Depends on**: U1 (cycle open check, identity/authz).

### U3. Evaluation & Scoring (backend)
- **Components**: Evaluation, Scoring & Aggregation
- **Responsibilities**: Idea-to-panel assignment, independent evaluation capture, weighted-sum scoring, on-read aggregation, deterministic ranking with tie-break. Primary PBT target (pure scoring/aggregation logic).
- **Depends on**: U1 (identity/authz, cycle state), U2 (ideas to evaluate).

### U4. Insights & Recognition (backend)
- **Components**: Dashboard & Analytics (read/query), Recognition, Notification
- **Responsibilities**: Read/query API for dashboards, leaderboards, analytics; top-3 recognition with tie-break and state transitions; email notifications (event-driven).
- **Depends on**: U3 (scores/aggregation/ranking), U1 (cycles, identity), U2 (idea data). Notification consumes domain events from U1/U3/U4.

### U5. Frontend (React SPA)
- **Type**: Separate React application unit
- **Responsibilities**: UI for all four roles — submission (drafts/submit), evaluation screens, admin configuration, dashboards/leaderboards/analytics, recognition display, authentication. Consumes the backend REST command API and read/query API. Automation-friendly (`data-testid`).
- **Depends on**: U1-U4 backend APIs.

---

## Greenfield Code Organization Strategy

Monorepo at workspace root (`/workshop/aisdlc/AIDLCProject`):

```
AIDLCProject/
├── backend/                         # Spring Boot modular monolith (single deployable)
│   ├── common-core/                 # U0: shared module
│   ├── platform-identity/           # U1
│   ├── ideas/                       # U2
│   ├── evaluation-scoring/          # U3
│   ├── insights-recognition/        # U4
│   ├── app/                         # application shell: wires modules, main(), config
│   ├── pom.xml (or build.gradle)    # multi-module build
│   └── Dockerfile
├── frontend/                        # U5: React SPA
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml               # local orchestration (frontend, backend, postgres)
└── aidlc-docs/                      # documentation only
```

- Backend units are multi-module Maven/Gradle modules compiled into one deployable (the `app` shell).
- Each backend module owns its packages: `controller` (API), `service`, `repository`, `domain`, `event`.
- `common-core` holds shared security/observability/event/persistence infrastructure.
- Frontend is independently built and Dockerized.
- Application code lives at workspace root (never in `aidlc-docs/`).

---

## Validation
- All 9 domain components are assigned to exactly one backend unit; cross-cutting concerns live in U0.
- Frontend is a single dedicated unit (U5).
- No unit has a circular dependency (see unit-of-work-dependency.md).
- Every user story maps to a primary unit (see unit-of-work-story-map.md).
