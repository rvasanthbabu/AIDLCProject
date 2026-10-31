# Domain Entities — Unit U2: Ideas

Technology-agnostic domain model for Idea Management. References User and Cycle (owned by U1) by id.

## Entity: Idea

| Attribute | Type | Notes |
|---|---|---|
| id | identifier | unique |
| submitterId | User ref | owner; set from authenticated principal |
| cycleId | Cycle ref? | null while Draft; set at submission (decision Q5=B) |
| title | string | required on submit, max 150 (Q3=A) |
| problemStatement | string | required on submit, max 4000 |
| proposedSolution | string | required on submit, max 4000 |
| expectedImpact | string | required on submit, max 4000 |
| category | IdeaCategory (enum) | required on submit |
| state | IdeaState (enum) | DRAFT, SUBMITTED, UNDER_EVALUATION, SCORED, RECOGNIZED, REJECTED |
| submittedAt | timestamp? | set on submit |
| createdAt / updatedAt | timestamp | audit |
| version | long | optimistic locking |

## Enum: IdeaCategory (fixed, Q2=A)
- `PROCESS`
- `PRODUCT`
- `COST_SAVING`
- `CUSTOMER_EXPERIENCE`
- `OTHER`

## Enum: IdeaState (full lifecycle, FR-8.1)
`DRAFT -> SUBMITTED -> UNDER_EVALUATION -> SCORED -> RECOGNIZED | REJECTED`

U2 owns transitions **DRAFT -> SUBMITTED** and deletion while DRAFT. Transitions into UNDER_EVALUATION (U3 assignment), SCORED (U3/U4), and RECOGNIZED/REJECTED (U4 recognition) are driven by other units but recorded on this entity. U2 exposes guarded mutators; other units call them through the Idea service boundary.

## Relationships
```
User (submitter) (1) ---- owns ----> (*) Idea
Cycle (1) ---- contains ----> (*) Idea   (only once submitted)
```

## Validation summary (enforced in service; see business-rules.md)
- Draft may be partially filled; submission requires all fields valid.
- Title <= 150; problem/solution/impact <= 4000 each.
- category in IdeaCategory.
