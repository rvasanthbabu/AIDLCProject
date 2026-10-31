# Functional Design Plan — Unit U3: Evaluation & Scoring

**Unit**: U3 Evaluation & Scoring (Evaluation + Scoring & Aggregation)
**Stories**: 2.1 (view assigned), 2.2 (score), 2.3 (independence), 2.4 (revise), 3.3 (assignment side), 6.1 (scoring/ranking input)
**Depends on**: U1 (identity/authz, cycle state, weights), U2 (ideas, markUnderEvaluation/markScored)
**Sources**: `requirements.md`, `application-design/*`, `business-rules.md` (U1 weights)

This unit holds the core scoring math — the primary property-based testing target. Please answer the embedded questions, then I'll generate the functional design.

---

## Part A: Clarifying Questions

### Question 1: Weighted Score Formula
Given metric scores (1-5 each) and global weights (summing to 100), how is one panel member's evaluation score computed?

A) weightedScore = (feasibility*wF + impact*wI + innovation*wN) / 100  (range 1-5, since weights sum to 100)

B) weightedScore = feasibility*wF + impact*wI + innovation*wN  (raw, range up to 500)

C) Normalized to 0-100 scale

X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

### Question 2: Aggregation Across Panel Members
How is an idea's aggregated score computed from multiple panel members' weighted scores?

A) Mean (average) of all submitted evaluations' weighted scores

B) Median of weighted scores

C) Mean, but only if a minimum number of evaluations exist (else "insufficient")

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 3: Per-Metric Aggregation (for comparative views)
For dashboards' per-metric breakdown (feasibility vs impact vs innovation), how are per-metric values aggregated?

A) Mean of each metric's raw 1-5 scores across evaluators

B) Sum of each metric across evaluators

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

### Question 4: Ranking & Tie-Break
Ranking for leaderboard/recognition uses aggregated score descending. Tie-break (per FR-6.2) is earliest submission. Any secondary tie-break if submission times are equal?

A) Idea id (stable, deterministic) as final tie-break

B) Alphabetical by title

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 5: Evaluation Completeness for Scoring
When is an idea considered "SCORED" (eligible for leaderboard/recognition)?

A) When all assigned panel members have submitted evaluations

B) When at least one evaluation exists (partial scoring counts)

C) When a configurable minimum number of evaluations is reached

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

### Question 6: Independence Enforcement
Story 2.3 requires independent evaluation. How strictly?

A) A panel member cannot see any other member's scores/comments for an idea at any time (even after submitting)

B) Cannot see others' scores before submitting their own; may see aggregates afterward

X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

### Question 7: Re-assignment effect on existing evaluations
If an admin removes an assignment after the panel member already scored, what happens to that evaluation?

A) Keep the evaluation (it still counts)

B) Discard the evaluation (removed assignment removes its score)

C) Disallow removing an assignment once an evaluation exists

X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Part B: Execution Checklist (runs after approval)

- [x] Generate `domain-entities.md` (Assignment, Evaluation entities; relationships to Idea/User/Cycle)
- [x] Generate `business-logic-model.md` (assignment, scoring, independence, revision, aggregation, ranking; mark-scored interplay with U2)
- [x] Generate `business-rules.md` (score bounds, weighted-sum, aggregation, ranking/tie-break, independence, assignment rules)
- [x] Identify PBT-01 testable properties (weighted-score bounds invariant, aggregation within min/max, ranking determinism + tie-break, round-trips)
- [x] Validate traceability to U3 stories and requirements
