# NFR Requirements — Unit U3: Evaluation & Scoring

Inherits platform NFRs. U3 is compute-sensitive because scoring/aggregation/ranking are read on dashboards (on-read aggregation; Application Design Q4=B).

## Performance & Scalability (addresses Application Design open item)
- **U3-NFR-PERF-1**: Dashboard/leaderboard reads compute aggregates on read. At enterprise scale this must stay performant:
  - Aggregation and ranking queries MUST be backed by indexes (`evaluations` by ideaId; `ideas` by (cycle_id, state)).
  - Aggregation SHOULD be pushed into SQL (AVG/SUM/COUNT group-by) rather than in-memory per idea.
  - **Mitigation if needed**: a short-TTL cache or a materialized aggregate (refresh on evaluation write) MAY be introduced in U4 if measured latency exceeds target. v1 default = SQL on-read; decision revisited in U4/Build-and-Test with load data.
- **U3-NFR-PERF-2**: Evaluation write (submit/revise) p95 < 300 ms.
- **U3-NFR-SCALE-1**: Stateless; scales with backend replicas.

## Security
- **U3-NFR-SEC-1**: Object-level authz — panel members access only actively assigned ideas (SECURITY-08).
- **U3-NFR-SEC-2**: Independence enforced in read paths (no peer scores pre-submission).
- **U3-NFR-SEC-3**: Metric range validation + parameterized queries (SECURITY-05).

## Reliability
- **U3-NFR-REL-1**: Transactional writes; optimistic locking on Evaluation for concurrent revision.
- **U3-NFR-REL-2**: Deterministic scoring/ranking (no reliance on wall-clock beyond submission timestamps).

## Testing (primary PBT unit)
- **U3-NFR-TEST-1**: PBT enforced (partial-mode blocking rules apply): normalized-score bounds & monotonicity, aggregate-within-bounds (PBT-03), Evaluation DTO round-trip (PBT-02), ranking determinism. jqwik (PBT-09).

## Tech Stack
- Inherits platform stack. Use `BigDecimal` for normalizedScore to avoid floating-point drift (deterministic, test-friendly).
