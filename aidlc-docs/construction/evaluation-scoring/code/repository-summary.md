# Repository Layer Summary — U3 Evaluation & Scoring

## Generated
- `AssignmentRepository`: findByIdeaIdAndPanelMemberId, findByPanelMemberIdAndCycleIdAndActiveTrue, existsByIdeaIdAndActiveTrue.
- `EvaluationRepository`: findByIdeaIdAndPanelMemberId, findByIdeaId, existsByIdeaId, plus JPQL aggregation (aggregateForIdeaRaw AVG/SUM/COUNT; aggregateByCycle group-by for U4 ranking).

## Schema (V3__evaluation_scoring.sql)
- assignments: unique (idea_id, panel_member_id); indexes (panel_member_id, cycle_id) and (idea_id).
- evaluations: unique (idea_id, panel_member_id); CHECK metrics 1-5; indexes (idea_id), (cycle_id).

## Notes
- All queries parameterized (SECURITY-05). Aggregation pushed to SQL (on-read performance).
- Testcontainers repository tests execute in Build and Test.
