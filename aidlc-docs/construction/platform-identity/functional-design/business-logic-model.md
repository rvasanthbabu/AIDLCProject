# Business Logic Model — Unit U1: Platform & Identity

Technology-agnostic business logic for authentication, authorization, user/role management, cycle lifecycle, and global criteria-weight configuration.

---

## 1. Authentication (Story 3.1, 8.7)

### Login flow
1. Receive email + password.
2. Look up active user by email; if not found or disabled → generic failure (no user enumeration; fail closed).
3. If `lockedUntil` is in the future → reject with lockout message.
4. Verify password against adaptive hash.
   - On failure: increment `failedLoginCount`; if threshold reached, set `lockedUntil` (progressive delay/lockout); log LOGIN_FAILED; return generic failure.
   - On success: reset `failedLoginCount`.
5. If user `role = ADMINISTRATOR` (MFA required) or `mfaEnabled = true`:
   - Return `mfaRequired = true` with a short-lived MFA challenge; do not issue full session yet.
   - Verify MFA code (TOTP) via `verifyMfa`; on success issue session.
6. Issue server-side session token (secure/httpOnly/sameSite cookie). Record login.

### SSO-ready abstraction (Story 8.7)
- `AuthenticationProvider` interface with a `LocalPasswordProvider` implementation in v1.
- Authorization and session logic depend on an abstract `Principal`, so a future `OidcProvider`/`SamlProvider` can be added without changing authorization.

### Session/token
- Validated server-side on every request (NFR-S6).
- Invalidated on logout; server-side expiration enforced.

## 2. Authorization (Story 3.2)

- `Principal` carries the single `Role`.
- `AuthorizationService.requirePermission(principal, action, resourceRef)`:
  - **Function-level**: action→allowed-roles map (deny by default). E.g., `CONFIGURE_WEIGHTS` → ADMINISTRATOR only; `EVALUATE` → PANEL_MEMBER; `SUBMIT_IDEA` → SUBMITTER; `VIEW_DASHBOARD` → all authenticated.
  - **Object-level**: delegated to owning unit (e.g., Idea ownership in U2, assignment check in U3) — U1 provides the primitive and role context.
- Applied as an API-layer guard plus in-service checks (defense in depth).

## 3. User & Role Management (Story 7.1)

- `createUser(actor=ADMIN, data)`: validate email uniqueness/format, assign exactly one role, set status ACTIVE. If role = ADMINISTRATOR, mark `mfaEnabled = true` (enrollment required on first login).
- `assignRole(actor=ADMIN, userId, role)`: change the single role; if new role = ADMINISTRATOR, require MFA enrollment; log USER_ROLE_CHANGED.
- `listUsers(actor=ADMIN, filter)`: paged.

## 4. Cycle Lifecycle (Story 7.3)

States: `DRAFT → OPEN → CLOSED`.

- `createCycle(actor=ADMIN, data)`: create in DRAFT with name, openDate, closeDate (closeDate > openDate).
- `openCycle(actor=ADMIN, cycleId)`:
  - Guard: no other cycle is OPEN (invariant, Q4=A). If one is OPEN → reject.
  - Transition DRAFT→OPEN; log CYCLE_OPENED.
- `closeCycle`:
  - **Manual** early close by Administrator (Q5=B): OPEN→CLOSED, set `closedAt=now`.
  - **Automatic** at `closeDate` (Q5=B): a scheduled check closes any OPEN cycle whose `closeDate <= now`; OPEN→CLOSED, `closedAt=closeDate`.
  - On close: publish `CycleClosedEvent(cycleId)` (consumed by Recognition in U4).
- `getActiveCycle()`: returns the single OPEN cycle or none.
- `isCycleOpen(cycleId)`: used by U2 (submission) and U3 (evaluation) to gate writes.

## 5. Criteria Weights Configuration (Story 7.2)

- Single global `CriteriaWeights` record (Q2=A).
- `getWeights()`: returns current weights.
- `setWeights(actor=ADMIN, feasibility, impact, innovation)`:
  - Validate each is an integer 0-100 and the three sum to exactly 100 (Q3=B).
  - Persist; log WEIGHTS_UPDATED (before/after). Stored as data (NFR-M3).
- Weights are consumed by U3 Scoring.

## 6. Error Handling
- All failures fail closed (deny).
- Generic user-facing messages; detailed context only in server logs (SECURITY-09/15).
- External/DB calls wrapped with error handling; transactions rolled back on error.

## 7. Events Published
- `CycleClosedEvent{ cycleId }` → Recognition (U4) eligibility.
(No other U1-published events; IdeaAssignedEvent originates in U3 when Admin triggers assignment.)

## 8. Testable Properties (PBT-01 identification)
For later PBT (partial mode — PBT-03 invariants are in scope):
- **Weight validation invariant**: `setWeights` accepts a triple iff all in [0,100] and sum == 100; rejects otherwise (invariant/boundary).
- **Single-open-cycle invariant**: after any sequence of create/open/close operations, at most one cycle is OPEN (stateful invariant — advisory under partial mode, documented).
- **Authorization determinism**: `requirePermission` yields the same allow/deny for identical (role, action) inputs (invariant).
- **Lockout monotonicity**: failedLoginCount only resets on success; lockout triggers at threshold (invariant).
