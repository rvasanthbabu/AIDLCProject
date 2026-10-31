# API Layer Summary — U1 Platform & Identity

## Endpoints
| Method | Path | Auth | Story |
|---|---|---|---|
| POST | /api/auth/login | public (rate-limited) | 3.1 |
| POST | /api/auth/mfa/verify | pending-MFA session | 3.1 |
| POST | /api/auth/logout | authenticated | 3.1 |
| POST | /api/admin/users | ADMINISTRATOR | 7.1 |
| PUT | /api/admin/users/{id}/role | ADMINISTRATOR | 7.1 |
| GET | /api/admin/weights | authenticated | 7.2 |
| PUT | /api/admin/weights | ADMINISTRATOR | 7.2 |
| POST | /api/cycles | ADMINISTRATOR | 7.3 |
| POST | /api/cycles/{id}/open | ADMINISTRATOR | 7.3 |
| POST | /api/cycles/{id}/close | ADMINISTRATOR | 7.3 |
| GET | /api/cycles/active | authenticated | 7.3 |

## Generated
- Controllers: `AuthController`, `AdminUserController`, `CriteriaWeightsController`, `CycleController`
- Principal/session: `CurrentUser`, `SecurityContextHelper`
- DTOs: `LoginRequest`, `MfaVerifyRequest`, `AuthResponse`, `CreateUserRequest`, `AssignRoleRequest`, `WeightsRequest`, `CreateCycleRequest`

## Security
- Deny-by-default; admin routes guarded with `@PreAuthorize("hasRole('ADMINISTRATOR')")` (function-level — SECURITY-08).
- Bean Validation on all request DTOs (SECURITY-05).
- Session-based auth with Secure/HttpOnly/SameSite cookies + CSRF; restricted CORS (SECURITY-08/12).
- Responses never expose passwordHash/mfaSecret.

## Tests
- Controller/authz tests planned to execute in Build and Test (require Spring context; MockMvc + spring-security-test). API DTO validation unit tests included.
