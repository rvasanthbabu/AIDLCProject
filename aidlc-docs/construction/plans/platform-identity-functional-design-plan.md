# Functional Design Plan — Unit U1: Platform & Identity

**Unit**: U1 Platform & Identity (Identity & Access, Administration, Cycle Management)
**Stories**: 3.1, 3.2, 3.3 (admin/assignment-trigger side), 7.1, 7.2, 7.3, 8.7
**Sources**: `unit-of-work.md`, `unit-of-work-story-map.md`, `application-design/*`, `requirements.md`

This plan defines the approach for detailed, technology-agnostic business logic design for U1. Please answer the embedded questions, then I'll generate the functional design artifacts.

---

## Part A: Clarifying Questions

### Question 1: User & Role Model
How should users and roles be modeled?

A) A user has exactly one role (single-role users)

B) A user can hold multiple roles simultaneously (e.g., someone is both Submitter and Panel Member)

C) Everyone is implicitly a Submitter; Panel Member / Administrator / Viewer are additional roles that can be assigned

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 2: Criteria & Weights Scope
How are evaluation criteria weights configured?

A) Global single set of weights applied to all cycles

B) Per-cycle weights (each cycle can have its own weights), defaulting from a global template

C) Per-cycle, fully independent each time

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 3: Weight Validation Rule
What constraint applies to the three metric weights (feasibility, impact, innovation)?

A) Each weight is a positive number; no sum constraint (weighted sum uses raw weights)

B) Weights must sum to 100 (percentages)

C) Weights must sum to 1.0 (normalized)

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

### Question 4: Cycle Concurrency
How many cycles can be open at once?

A) Exactly one open cycle at a time (opening a new one requires the previous to be closed)

B) Multiple cycles can be open concurrently

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 5: Cycle Closure Trigger
How does a cycle close?

A) Manual close by Administrator only

B) Automatic at the close date, with manual early-close option

C) Automatic at close date only

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

### Question 6: Password Policy Specifics
Beyond the security baseline (min 8 chars, breached-password check, adaptive hashing), any additional password rules?

A) Baseline only (min 8, breached-list check)

B) Baseline + complexity (upper/lower/number/symbol) and periodic rotation

C) Baseline + complexity, no forced rotation (modern NIST-aligned)

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

### Question 7: MFA Scope
MFA is required for admins per the security baseline. For other roles?

A) MFA for Administrators only (others optional/off in v1)

B) MFA available (opt-in) for all roles, required for Administrators

C) MFA required for Administrators and Panel Members

X) Other (please describe after [Answer]: tag below)

[Answer]: MFA Not required

---

## Part B: Workflow Efficiency Preference (applies to Construction phase)

### Question 8: Construction Approval Cadence
The Construction phase normally pauses for your approval after each stage (Functional Design, NFR Requirements, NFR Design, Infrastructure Design, Code Generation) for each of the 5 units — about 25 gates. How would you like to proceed?

A) Keep every gate (approve after each stage of each unit) — maximum control

B) Per-unit batching: I complete all design stages (Functional + NFR Req + NFR Design + Infra) for a unit, then pause once for your approval before Code Generation, and once after Code Generation

C) Design-phase batching: I complete Functional Design for a unit and pause (keep code generation gates), but combine the NFR/Infra design docs into that same review

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

## Part C: Execution Checklist (runs after approval)

- [x] Generate `domain-entities.md` (User, Role, Cycle, Criteria/Weights, and relationships)
- [x] Generate `business-logic-model.md` (auth flow, authorization model, cycle lifecycle, admin config workflows)
- [x] Generate `business-rules.md` (validation, constraints, state-transition guards, password/MFA rules)
- [x] Identify PBT-01 testable properties for U1 (e.g., weight validation invariants, role-permission checks)
- [x] Validate traceability to U1 stories and requirements
- Note: U1 is a backend unit; admin/login UI is owned by U5 Frontend, so no `frontend-components.md` here.
