# Business Rules — Unit U3: Evaluation & Scoring

## Assignment
- **BR-U3-01**: Only ADMINISTRATOR may create/remove assignments (SECURITY-08).
- **BR-U3-02**: A panel member assigned to an idea MUST have the PANEL_MEMBER role; assignment to non-panel roles MUST be rejected.
- **BR-U3-10**: An idea MUST be assignable only while its cycle is OPEN and the idea is SUBMITTED or UNDER_EVALUATION; first assignment transitions the idea to UNDER_EVALUATION (via U2).
- **BR-U3-11**: Removing an assignment MUST set it inactive but MUST NOT delete any existing evaluation (Q7=A); that evaluation continues to count.
- **BR-U3-12**: (ideaId, panelMemberId) MUST be unique for assignments and for evaluations.

## Scoring
- **BR-U3-03**: A panel member MUST only view/evaluate ideas actively assigned to them (object-level authz; NFR-S6).
- **BR-U3-04**: Submitting/revising an evaluation MUST require an active assignment of that idea to the actor.
- **BR-U3-05**: Evaluations MUST only be submitted/revised while the cycle is OPEN (BR mirrors FR-2.6).
- **BR-U3-06**: Each metric (feasibility, impact, innovation) MUST be an integer in [1,5]; out-of-range MUST be rejected (SECURITY-05).
- **BR-U3-07**: After cycle close, evaluations MUST be immutable.
- **BR-U3-08**: Independence — a panel member MUST NOT see other members' individual scores/comments for an idea before submitting their own; aggregates MAY be shown afterward (Q6=B).

## Scoring Math (pure, deterministic)
- **BR-U3-20**: weightedSum = feasibility*wF + impact*wI + innovation*wN, using current global weights (wF+wI+wN=100).
- **BR-U3-21**: normalizedScore = (weightedSum - 100) / 400 * 100, yielding a value in [0,100] (Q1=C). normalizedScore MUST be computed with sufficient decimal precision (not integer-truncated).
- **BR-U3-22**: aggregateScore = arithmetic mean of evaluations' normalizedScore (Q2=A); undefined (null) when there are no evaluations.
- **BR-U3-23**: Per-metric comparative values are the SUM of each metric across evaluations (Q3=B).

## Scored Eligibility & Ranking
- **BR-U3-24**: An idea becomes SCORED once at least one evaluation exists (Q5=B); the SCORED transition is applied via U2.
- **BR-U3-25**: rankIdeas MUST order by aggregateScore DESC, then idea.submittedAt ASC (earliest first; FR-6.2), then ideaId ASC (Q4=A). The ordering MUST be total and deterministic.

## Integrity / Errors
- **BR-U3-26**: All mutations MUST be transactional and parameterized; errors fail closed with generic messages (SECURITY-05/09/15).

## Traceability
| Rule(s) | Story | Requirement |
|---|---|---|
| BR-U3-01,02,10,11,12 | 3.3 | FR-3.2, FR-8.2 |
| BR-U3-03,04,05,06,07 | 2.1, 2.2, 2.4 | FR-2.1, FR-2.2, FR-2.6, NFR-S4/S6 |
| BR-U3-08 | 2.3 | FR-2.4 |
| BR-U3-20,21,22,23 | 2.2, 4.x | FR-2.3, FR-4.4 |
| BR-U3-24,25 | 6.1 | FR-4.2, FR-6.1, FR-6.2 |
| BR-U3-26 | all | SECURITY-05/09/15 |
