# Domain Entities — Unit U3: Evaluation & Scoring

References Idea (U2), User/Cycle (U1) by id.

## Entity: Assignment
A panel member is assigned to evaluate a specific idea in a cycle.

| Attribute | Type | Notes |
|---|---|---|
| id | identifier | unique |
| ideaId | Idea ref | assigned idea |
| panelMemberId | User ref | must have PANEL_MEMBER role |
| cycleId | Cycle ref | the idea's cycle |
| assignedBy | User ref (ADMINISTRATOR) | |
| assignedAt | timestamp | |
| active | boolean | false if assignment removed (evaluation kept — Q7=A) |

Constraint: unique (ideaId, panelMemberId) — a member is assigned an idea at most once.

## Entity: Evaluation
A panel member's scores for an assigned idea.

| Attribute | Type | Notes |
|---|---|---|
| id | identifier | unique |
| ideaId | Idea ref | |
| panelMemberId | User ref | |
| cycleId | Cycle ref | |
| feasibility | int | 1-5 |
| impact | int | 1-5 |
| innovation | int | 1-5 |
| normalizedScore | decimal | derived, 0-100 (stored for convenience; always recomputable) |
| comment | string? | optional, max length bounded |
| submittedAt | timestamp | first submission |
| updatedAt | timestamp | last revision (editable until cycle close) |
| version | long | optimistic locking |

Constraint: unique (ideaId, panelMemberId) — one evaluation per member per idea.

## Value Object: AggregateResult (computed on read; not persisted)
| Field | Type | Notes |
|---|---|---|
| ideaId | Idea ref | |
| aggregateScore | decimal | mean of evaluations' normalizedScore (Q2=A); null if no evaluations |
| feasibilitySum | int | SUM of feasibility across evaluations (Q3=B) |
| impactSum | int | SUM of impact |
| innovationSum | int | SUM of innovation |
| evaluatorCount | int | number of submitted evaluations |

## Value Object: RankedIdea (computed on read)
| Field | Type | Notes |
|---|---|---|
| ideaId | Idea ref | |
| aggregateScore | decimal | |
| submittedAt | timestamp | idea submission time (for tie-break) |
| rank | int | 1-based |

## Relationships
```
Idea (1) ---- has ----> (*) Assignment ---- to ----> User(PANEL_MEMBER)
Idea (1) ---- has ----> (*) Evaluation ---- by ----> User(PANEL_MEMBER)
```
