# API Layer Summary — U2 Ideas

## Endpoints (all require SUBMITTER role; ownership enforced in service)
| Method | Path | Story |
|---|---|---|
| POST | /api/ideas | 1.1 (create draft) |
| PUT | /api/ideas/{id} | 1.1 (update draft) |
| POST | /api/ideas/{id}/submit | 1.2 (submit) |
| GET | /api/ideas/mine | 1.3 (list my ideas, paged) |
| GET | /api/ideas/{id} | 1.3 (view one) |
| DELETE | /api/ideas/{id} | 1.1 (delete draft) |

## Generated
- `IdeaController` with `@PreAuthorize("hasRole('SUBMITTER')")`
- DTOs: `IdeaRequest` (validated, length-bounded), `IdeaResponse`, `IdeaSummaryResponse`

## Security
- Role guard at API (SECURITY-08); ownership via submitter-scoped service lookups (IDOR prevention — NFR-S6); Bean Validation (SECURITY-05).

## Tests
- Controller/authz tests execute in Build and Test (MockMvc + spring-security-test).
