# Business Rules — Unit U2: Ideas

Enumerated, testable rules. "MUST" rules enforced server-side; fail closed.

## Authorization & Ownership
- **BR-U2-01**: Creating/submitting an idea MUST require the SUBMITTER role (BR-U1-11; SECURITY-08).
- **BR-U2-02**: Editing/deleting a draft MUST require the requester to be the idea's submitter (object-level authorization; prevent IDOR — NFR-S6).
- **BR-U2-03**: `listMyIdeas`/`getIdea` (submitter view) MUST return only ideas owned by the requester.

## Validation
- **BR-U2-04**: On submission, title, problemStatement, proposedSolution, expectedImpact, and category MUST all be present and valid.
- **BR-U2-08**: Title MUST be <= 150 chars; problemStatement/proposedSolution/expectedImpact MUST each be <= 4000 chars (Q3=A). Drafts may be partial but MUST still respect max lengths.
- **BR-U2-09**: category MUST be one of the fixed IdeaCategory values (Q2=A).

## State & Lifecycle
- **BR-U2-05**: Draft edits/deletes MUST only be allowed while state = DRAFT.
- **BR-U2-06**: Submission MUST require an OPEN cycle (via U1); if none is open, submission MUST be rejected. The idea binds to that cycle at submission (Q5=B).
- **BR-U2-07**: After SUBMITTED, the submitter MUST NOT edit or delete the idea (locked; Q4=A, Q6=A).
- **BR-U2-10**: Idea state transitions MUST follow DRAFT -> SUBMITTED -> UNDER_EVALUATION -> SCORED -> (RECOGNIZED | REJECTED); no skips or reversals. U2 owns DRAFT->SUBMITTED; later transitions are invoked by U3/U4 through the Idea service boundary and MUST validate the legal source state.

## Integrity / Errors
- **BR-U2-11**: All idea mutations MUST be transactional and use parameterized persistence (SECURITY-05).
- **BR-U2-12**: Authorization/state errors MUST fail closed with generic messages (SECURITY-09/15).

## Traceability
| Rule(s) | Story | Requirement |
|---|---|---|
| BR-U2-01,02,03 | 1.1, 1.3 | FR-1.4, NFR-S6 |
| BR-U2-04,08,09 | 1.2 | FR-1.1, FR-1.5, NFR-S4 |
| BR-U2-05,06,07,10 | 1.1, 1.2 | FR-1.2, FR-1.3, FR-7.3, FR-8.1 |
| BR-U2-11,12 | all | SECURITY-05/09/15 |
