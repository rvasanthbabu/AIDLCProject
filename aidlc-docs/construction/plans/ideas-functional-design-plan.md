# Functional Design Plan — Unit U2: Ideas

**Unit**: U2 Ideas (Idea Management)
**Stories**: 1.1 (create/save draft), 1.2 (submit), 1.3 (view my ideas and status)
**Depends on**: U1 (identity/authz, cycle open gating)
**Sources**: `unit-of-work.md`, `requirements.md`, `application-design/*`

Please answer the embedded questions, then I'll generate U2's functional design artifacts.

---

## Part A: Clarifying Questions

### Question 1: Idea Submission Fields
Which structured fields should an idea capture? (confirm the set)

A) Title, Problem Statement, Proposed Solution, Expected Impact, Category — all text except Category (from a list)

B) Option A plus optional Attachments/Links (URLs)

C) Minimal: Title, Description, Category only

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 2: Category Source
How is an idea's Category determined?

A) Fixed enum defined in code (e.g., Process, Product, CostSaving, Other)

B) Admin-configurable list of categories (data-driven)

C) Free text entered by submitter

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 3: Field Length / Validation Limits
What validation limits for text fields?

A) Title <= 150 chars; each long text field (problem/solution/impact) <= 4000 chars; required on submit

B) Title <= 200; long text <= 10000; required on submit

C) You specify limits

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 4: Draft Editing After Submission
Can a submitter edit an idea after it is Submitted?

A) No — once Submitted, it is locked (edits only while Draft)

B) Yes — editable until the idea enters Under Evaluation (assigned)

C) Yes — editable any time while the cycle is open

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 5: Draft Cycle Association
When is a draft associated with a cycle?

A) At draft creation — a draft belongs to the currently open cycle; if no cycle is open, drafting is not allowed

B) Drafts are cycle-independent; a cycle is assigned at submission time (submit requires an open cycle)

C) Drafts allowed anytime; submission requires an open cycle and binds to it

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

### Question 6: Withdraw / Delete
Can a submitter withdraw or delete an idea?

A) Delete allowed only while Draft; no withdraw after submission

B) Delete while Draft; withdraw (soft) allowed before Under Evaluation

C) No delete/withdraw in v1

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Part B: Execution Checklist (runs after approval)

- [x] Generate `domain-entities.md` (Idea entity, state, fields, relationships to Cycle/User)
- [x] Generate `business-logic-model.md` (draft/submit/view flows, cycle gating, ownership)
- [x] Generate `business-rules.md` (validation, state transitions, ownership/authorization rules)
- [x] Identify PBT-01 testable properties for U2 (state-transition guards, Idea DTO round-trip, ownership invariant)
- [x] Validate traceability to U2 stories and requirements
