# Unit-to-Story Map — Ideation Portal

Every user story is assigned to exactly one **primary** unit (where its core logic lives). The Frontend unit (U5) provides the UI for most stories; that UI involvement is noted in the "Frontend (U5) involvement" column but the primary/backend owner is the authoritative unit for the story's logic. Cross-cutting technical stories (Epic 8) map to U0 or the relevant backend unit.

---

## Mapping Table

| Story | Title | Primary Unit | Frontend (U5) involvement |
|---|---|---|---|
| 1.1 | Create and save an idea draft | U2 Ideas | Yes |
| 1.2 | Submit a completed idea | U2 Ideas | Yes |
| 1.3 | View my ideas and status | U2 Ideas | Yes |
| 2.1 | View ideas assigned to me | U3 Evaluation & Scoring | Yes |
| 2.2 | Score an idea across three metrics | U3 Evaluation & Scoring | Yes |
| 2.3 | Evaluate independently | U3 Evaluation & Scoring | Yes |
| 2.4 | Revise my evaluation before close | U3 Evaluation & Scoring | Yes |
| 3.1 | Authenticate with email/password | U1 Platform & Identity | Yes (login UI) |
| 3.2 | Enforce role-based authorization | U1 Platform & Identity (+ U0 filters) | Indirect |
| 3.3 | Admin assigns ideas to panel members | U1 Platform & Identity (Admin) → U3 (Assignment) | Yes (admin UI) |
| 4.1 | View aggregated scores on dashboard | U4 Insights & Recognition | Yes |
| 4.2 | View leaderboard ranking | U4 Insights & Recognition | Yes |
| 4.3 | Compare ideas across dimensions | U4 Insights & Recognition | Yes |
| 5.1 | Highlight top-performing ideas | U4 Insights & Recognition | Yes |
| 5.2 | Compare performance across criteria | U4 Insights & Recognition | Yes |
| 6.1 | Identify top 3 with tie-breaking | U4 Insights & Recognition (+ U3 scoring) | Indirect |
| 6.2 | Award recognition and notify | U4 Insights & Recognition | Yes (display) |
| 7.1 | Manage users and roles | U1 Platform & Identity | Yes (admin UI) |
| 7.2 | Configure evaluation criteria and weights | U1 Platform & Identity (Admin) | Yes (admin UI) |
| 7.3 | Create, open, and close cycles | U1 Platform & Identity (Cycle) | Yes (admin UI) |
| 8.1 | Secure data and transport | U0 Common/Core (+ Infra) | No |
| 8.2 | Input validation & injection prevention | U0 Common/Core (applied per unit) | No |
| 8.3 | HTTP security headers & hardening | U0 Common/Core | No |
| 8.4 | Observability, logging, alerting | U0 Common/Core (+ Infra) | No |
| 8.5 | HA & resilient dependencies | U0 Common/Core (+ Infra) | No |
| 8.6 | Backups and recovery | Infra (Infrastructure Design) | No |
| 8.7 | SSO-ready auth abstraction | U1 Platform & Identity | No |
| 8.8 | Secure CI/CD with canary & auto-rollback | Infra (Infrastructure Design) + CI | No |

---

## Coverage by Unit

| Unit | Primary Stories |
|---|---|
| U0 Common/Core | 8.1, 8.2, 8.3, 8.4, 8.5 (cross-cutting, applied across units) |
| U1 Platform & Identity | 3.1, 3.2, 3.3 (admin side), 7.1, 7.2, 7.3, 8.7 |
| U2 Ideas | 1.1, 1.2, 1.3 |
| U3 Evaluation & Scoring | 2.1, 2.2, 2.3, 2.4, 3.3 (assignment side), 6.1 (scoring input) |
| U4 Insights & Recognition | 4.1, 4.2, 4.3, 5.1, 5.2, 6.1, 6.2 |
| U5 Frontend | UI for all user-facing stories (1.x, 2.x, 3.1/3.3, 4.x, 5.x, 6.2, 7.x) |
| Infrastructure Design | 8.6, 8.8 (infra/CI concerns, elaborated in Infrastructure Design) |

---

## Validation
- All 26 stories are assigned a primary owner.
- Story 3.3 spans admin (U1) + assignment logic (U3); primary logic owner noted; both units coordinate via service call.
- Story 6.1 uses U3 scoring but is owned by U4 (recognition computation); dependency is satisfied by build order (U3 before U4).
- Infra-only stories (8.6, 8.8) are owned by Infrastructure Design (Construction), not a backend code unit.
- No story is left unassigned.
