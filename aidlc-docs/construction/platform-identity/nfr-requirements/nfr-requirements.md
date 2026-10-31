# NFR Requirements — Unit U1: Platform & Identity

Derived from project requirements (`requirements.md`) and enabled extensions. U1 is the security- and identity-critical foundation, so security NFRs are especially prominent.

## Scalability
- **U1-NFR-SCALE-1**: Support enterprise scale (thousands of users). Authentication and authorization are on the hot path of every request — must be stateless and horizontally scalable.
- **U1-NFR-SCALE-2**: Login spikes expected at cycle open/close; auth endpoints must tolerate bursts (rate limiting + horizontal scale).

## Performance
- **U1-NFR-PERF-1**: Token validation (per-request authorization) p95 < 10 ms (in-process, no remote call per request).
- **U1-NFR-PERF-2**: Login p95 < 500 ms excluding MFA user input.

## Availability
- **U1-NFR-AVAIL-1**: Inherits platform target 99.95% (NFR-R1). Auth outage blocks the whole platform → highest criticality within the monolith.
- **U1-NFR-AVAIL-2**: Single-region, multi-zone; stateless app tier across ≥2 AZs (NFR-R3).

## Security (Security Baseline — blocking)
- **U1-NFR-SEC-1**: Adaptive password hashing, breached-password check, complexity policy, no forced rotation (SECURITY-12; BR-U1-02/03).
- **U1-NFR-SEC-2**: MFA (TOTP) supported; required for Administrators (SECURITY-12; BR-U1-07).
- **U1-NFR-SEC-3**: Secure session cookies, server-side validation/expiry, logout invalidation (SECURITY-12).
- **U1-NFR-SEC-4**: Brute-force protection (lockout/progressive delay) (SECURITY-12).
- **U1-NFR-SEC-5**: Deny-by-default authorization, server-side role checks (SECURITY-08).
- **U1-NFR-SEC-6**: Input validation on all auth/admin endpoints; parameterized queries (SECURITY-05).
- **U1-NFR-SEC-7**: Append-only audit logging of security/critical events; no secrets/PII in logs (SECURITY-03/13/14).
- **U1-NFR-SEC-8**: Secrets (MFA secrets, hashing pepper if any) in a secrets manager; no hardcoded credentials (SECURITY-12).
- **U1-NFR-SEC-9**: Rate limiting on public auth endpoints (SECURITY-11).

## Reliability / Resiliency
- **U1-NFR-REL-1**: All DB calls have explicit timeouts; fail closed on error (RESILIENCY-10, SECURITY-15).
- **U1-NFR-REL-2**: Shallow + deep health checks (DB connectivity) exposed (RESILIENCY-06).
- **U1-NFR-REL-3**: Scheduled cycle auto-close job must be idempotent and safe under multi-instance (only one instance effectively closes; CycleClosedEvent published once — BR-U1-18).

## Maintainability
- **U1-NFR-MAINT-1**: Weights and policy thresholds are configuration, not hardcoded (NFR-M3).
- **U1-NFR-MAINT-2**: Auth provider abstraction to allow future SSO (FR-9.2).

## Testing
- **U1-NFR-TEST-1**: PBT (partial) for invariant properties: weight-sum=100 validation (PBT-03), authorization determinism, lockout monotonicity. Framework: jqwik (PBT-09).
