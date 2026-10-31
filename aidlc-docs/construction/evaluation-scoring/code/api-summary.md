# API Layer Summary — U3 Evaluation & Scoring

## Endpoints
| Method | Path | Auth | Story |
|---|---|---|---|
| POST | /api/assignments | ADMINISTRATOR | 3.3 |
| DELETE | /api/assignments/{id} | ADMINISTRATOR | 3.3 |
| PUT | /api/ideas/{ideaId}/evaluation | PANEL_MEMBER | 2.2, 2.4 |
| GET | /api/ideas/{ideaId}/evaluation | PANEL_MEMBER | 2.1, 2.3 |

## Generated
- `AssignmentController` (admin), `EvaluationController` (panel)
- DTOs: `AssignRequest`, `EvaluationRequest` (metrics 1-5 validated), `EvaluationResponse`, `AggregateResponse`

## Security
- Role guards via `@PreAuthorize`; active-assignment + ownership enforced in service (SECURITY-08).
- Independence: GET returns only the caller's own evaluation (never peers') (FR-2.4).
- Bean Validation on inputs (SECURITY-05).

## Tests
- Controller/authz/validation tests execute in Build and Test.
