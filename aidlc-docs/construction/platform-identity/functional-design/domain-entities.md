# Domain Entities — Unit U1: Platform & Identity

Technology-agnostic domain model for Identity & Access, Administration, and Cycle Management. Attributes are logical; concrete column types/DDL are defined in Code Generation / migrations.

---

## Entity: User
Represents a platform account.

| Attribute | Type | Notes |
|---|---|---|
| id | identifier | unique |
| email | string | unique, used for login, validated format |
| passwordHash | string | adaptive hash (never plaintext); not exposed via API |
| displayName | string | |
| role | Role (enum) | exactly one role per user (decision Q1=A) |
| mfaEnabled | boolean | required true for ADMINISTRATOR; optional otherwise |
| mfaSecret | string? | present only if MFA enrolled; secret-managed, never logged |
| status | enum | ACTIVE, DISABLED |
| failedLoginCount | int | for brute-force protection |
| lockedUntil | timestamp? | set during lockout |
| createdAt / updatedAt | timestamp | audit |

## Enum: Role
- `SUBMITTER` — can create/submit/track own ideas
- `PANEL_MEMBER` — can evaluate assigned ideas
- `ADMINISTRATOR` — full configuration + management; MFA required
- `VIEWER` — read-only dashboards/analytics

## Entity: Cycle
Represents a time-boxed evaluation campaign.

| Attribute | Type | Notes |
|---|---|---|
| id | identifier | unique |
| name | string | |
| state | enum | DRAFT, OPEN, CLOSED |
| openDate | timestamp | when it opens / became open |
| closeDate | timestamp | scheduled auto-close time |
| closedAt | timestamp? | actual close time (may be earlier if manually closed) |
| createdBy | User ref (ADMINISTRATOR) | |
| createdAt / updatedAt | timestamp | audit |

Invariant: at most one Cycle in state OPEN at any time (decision Q4=A).

## Entity: CriteriaWeights
Global configuration for the three evaluation metrics (decision Q2=A — single global set).

| Attribute | Type | Notes |
|---|---|---|
| id | identifier | single active record (global) |
| feasibilityWeight | integer (percent) | 0-100 |
| impactWeight | integer (percent) | 0-100 |
| innovationWeight | integer (percent) | 0-100 |
| updatedBy | User ref (ADMINISTRATOR) | |
| updatedAt | timestamp | audit |

Invariant: feasibilityWeight + impactWeight + innovationWeight = 100 (decision Q3=B).

## Entity: AuditLogEntry (shared, owned at platform level)
Append-only record of security/critical actions (supports SECURITY-13/14).

| Attribute | Type | Notes |
|---|---|---|
| id | identifier | |
| actorUserId | User ref | who |
| action | string | e.g., USER_ROLE_CHANGED, CYCLE_OPENED, WEIGHTS_UPDATED, LOGIN_FAILED |
| targetRef | string? | affected entity |
| details | json | before/after where relevant (no secrets/PII) |
| timestamp | timestamp | append-only |

## Relationships
```
User (1) ---- creates ----> (*) Cycle
User (ADMINISTRATOR) ---- updates ----> (1) CriteriaWeights
User (*) ---- generates ----> (*) AuditLogEntry
```
(CriteriaWeights is global; Cycle references its creator. Ideas/Evaluations belong to other units and reference Cycle/User by id.)

## Cross-Unit References (not owned by U1)
- Idea, Assignment, Evaluation, Recognition reference `User.id` and `Cycle.id` but are owned by U2/U3/U4.
