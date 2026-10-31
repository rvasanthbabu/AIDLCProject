# Business Rules — Unit U1: Platform & Identity

Enumerated, testable business rules with IDs. "MUST" rules are enforced server-side and fail closed.

---

## Identity & Authentication

- **BR-U1-01**: Email MUST be unique across users and match a valid email format.
- **BR-U1-02**: Passwords MUST be at least 8 characters, satisfy complexity (at least one uppercase, one lowercase, one digit, one symbol), and be rejected if found on a breached-password list (Q6=C + SECURITY-12). No forced periodic rotation.
- **BR-U1-03**: Passwords MUST be stored using an adaptive hashing algorithm; plaintext/weak hashes are prohibited. passwordHash is never returned by any API or written to logs.
- **BR-U1-04**: After N consecutive failed logins (configurable, default 5), the account MUST be locked for a cooldown period (progressive delay/lockout). failedLoginCount resets on successful login.
- **BR-U1-05**: Login failures MUST return a generic error (no indication of whether email exists) and MUST be logged (LOGIN_FAILED) without the attempted password.
- **BR-U1-06**: Sessions MUST be server-side validated on every request, use Secure/HttpOnly/SameSite cookies, expire server-side, and be invalidated on logout.

## MFA (SECURITY-12; Clarification 1 = A)

- **BR-U1-07**: MFA MUST be supported (TOTP). Administrators MUST have MFA enabled and complete MFA at login. Other roles MAY enable MFA (optional in v1).
- **BR-U1-08**: An ADMINISTRATOR account without completed MFA enrollment MUST be forced to enroll before gaining admin access.
- **BR-U1-09**: MFA secrets MUST be stored via the secrets mechanism and never logged or returned by APIs.

## Roles & Authorization

- **BR-U1-10**: Each user MUST have exactly one role (SUBMITTER, PANEL_MEMBER, ADMINISTRATOR, VIEWER) (Q1=A).
- **BR-U1-11**: Idea submission requires the SUBMITTER role (strict single-role; Clarification 2 = A).
- **BR-U1-12**: Authorization MUST be deny-by-default; every action maps to an allowed-role set, checked server-side (function-level). Admin/privileged actions MUST verify ADMINISTRATOR role server-side.
- **BR-U1-13**: Only ADMINISTRATOR may manage users/roles, configure weights, manage cycles, assign ideas, and trigger recognition.

## Cycles

- **BR-U1-14**: A cycle's closeDate MUST be strictly after its openDate.
- **BR-U1-15**: At most one cycle MAY be in state OPEN at any time; opening a cycle MUST be rejected if another is OPEN (Q4=A).
- **BR-U1-16**: Cycle state transitions MUST follow DRAFT → OPEN → CLOSED only; no backward transitions.
- **BR-U1-17**: A cycle MUST close automatically when now >= closeDate; an Administrator MAY close an OPEN cycle early (Q5=B). closedAt records the actual close time.
- **BR-U1-18**: Closing a cycle MUST publish CycleClosedEvent exactly once for that cycle.

## Criteria Weights

- **BR-U1-19**: Each metric weight (feasibility, impact, innovation) MUST be an integer in [0, 100].
- **BR-U1-20**: The three weights MUST sum to exactly 100 (Q3=B). Updates violating this MUST be rejected.
- **BR-U1-21**: Weights are global (single active record) and apply to all cycles (Q2=A). Changes MUST be logged (WEIGHTS_UPDATED) with before/after values.

## Auditing & Errors

- **BR-U1-22**: Security/critical actions (role change, cycle open/close, weight update, login failure, MFA events) MUST be written to the append-only audit log with actor, timestamp, and non-sensitive details.
- **BR-U1-23**: All error paths MUST fail closed (deny access/halt), roll back transactions, and return generic user-facing messages.

---

## Traceability
| Rule(s) | Story | Requirement |
|---|---|---|
| BR-U1-01..06 | 3.1 | FR-9.1, NFR-S10 |
| BR-U1-07..09 | 3.1 | NFR-S10 (SECURITY-12) |
| BR-U1-10..13 | 3.2, 7.1 | FR-3.1, FR-3.4, FR-9.3, NFR-S6 |
| BR-U1-14..18 | 7.3 | FR-7.1, FR-7.2, FR-7.3, FR-7.4 |
| BR-U1-19..21 | 7.2 | FR-2.3, NFR-M3 |
| BR-U1-22..23 | 3.2, all | SECURITY-13/14/09/15 |
