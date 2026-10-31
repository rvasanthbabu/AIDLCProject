# Functional Design — Clarification Questions (Unit U1)

I found one blocking item and one item to confirm before generating U1's functional design.

---

## Clarification 1: MFA vs Security Baseline (BLOCKING)

You answered Q7 "MFA Not required". However, you enabled the **Security Baseline** extension as **blocking**, and rule **SECURITY-12** states that **MFA MUST be supported for administrative accounts** (and SHOULD be available to all users). "No MFA at all" is a non-compliant, blocking security finding — I can't proceed with it while the Security Baseline is enforced.

How would you like to resolve this?

A) Keep the Security Baseline as-is: **support MFA, require it for Administrators**, optional for other roles (satisfies SECURITY-12). MFA enforcement for non-admins stays off in v1.

B) Support MFA capability in the design but make it **configurable/disabled by default**, with Administrator MFA togglable — documented as a deviation from SECURITY-12 that you explicitly accept (I will record the accepted-risk exception in the audit log).

C) **Disable the Security Baseline MFA requirement specifically** (narrow exception to SECURITY-12 only), recorded as an accepted risk; rest of Security Baseline stays enforced.

D) Disable the entire Security Baseline extension (not recommended — removes all blocking security rules).

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Clarification 2: Single-Role Model Confirmation (confirm)

You answered Q1 "single role per user". In many ideation programs, an employee who submits ideas might also serve on an evaluation panel. With a strict single-role model, such a person would need separate accounts, OR submission is treated as something only the "Submitter" role can do.

Please confirm the intended model:

A) Strict single role — each account has exactly one role; a person needing two capabilities has two accounts. "Submit idea" requires the Submitter role.

B) Single role, but **any authenticated user can submit ideas** regardless of role (submission is a baseline capability; the named roles govern evaluate/admin/view). (This keeps a single "primary role" while not blocking participation.)

X) Other (please describe after [Answer]: tag below)

[Answer]: A
