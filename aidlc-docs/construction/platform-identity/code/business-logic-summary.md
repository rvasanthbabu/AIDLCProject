# Business Logic Summary — U1 Platform & Identity

## Generated (application code at workspace root under `backend/`)

### common-core (U0 shared foundation)
- `core/security/SecurityHeadersFilter.java` — CSP/HSTS/X-Content-Type-Options/X-Frame-Options/Referrer-Policy (SECURITY-04)
- `core/security/RateLimiter.java` — in-process token bucket (SECURITY-11)
- `core/observability/CorrelationIdFilter.java` — correlation IDs in MDC (SECURITY-03)
- `core/error/DomainException.java`, `ApiError.java`, `GlobalExceptionHandler.java` — fail-closed, generic errors (SECURITY-09/15)
- `core/event/DomainEvent.java`, `DomainEventPublisher.java` — in-process event bus (Q7=B)
- `core/audit/AuditLogEntry.java`, `AuditLogRepository.java`, `AuditLogWriter.java` — append-only audit (SECURITY-13/14)

### platform-identity (U1)
- Domain: `User`, `Role`, `UserStatus`, `Cycle`, `CycleState`, `CriteriaWeights`
- Pure logic (PBT targets): `WeightsValidator` (sum=100), `PermissionCatalog` (deny-by-default), `PasswordPolicy`
- Services: `AuthenticationService` (+ `auth/AuthenticationProvider`, `LocalPasswordProvider`, `TotpService`, `AuthResult`, `AuthProperties`), `AuthorizationService`, `UserAccountService`, `CycleService`, `CriteriaConfigService`, `ScheduledCloseWorker`
- Event: `CycleClosedEvent`
- Config: `IdentityBeansConfig` (BCrypt, breached checker), `SecurityConfig` (sessions, CORS, deny-by-default)

## Rules implemented
BR-U1-01..23 (identity, MFA for admins, single role, cycle state machine + single-open invariant + auto/manual close, weights sum=100, audit, fail-closed).

## PBT (partial mode)
- `WeightsValidatorPropertyTest` (PBT-03 invariant)
- `PermissionCatalogPropertyTest` (authorization determinism)
- Example-based: `PasswordPolicyTest`, `CycleStateMachineTest`

## Notes
- Build/test execution deferred to the Build and Test stage (no JDK/Maven in the authoring environment). Code was manually reviewed for type/wiring consistency.
