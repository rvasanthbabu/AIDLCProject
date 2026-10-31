package com.ideationportal.evaluation.repository;

import com.ideationportal.evaluation.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluationRepository extends JpaRepository<Evaluation, UUID> {

    Optional<Evaluation> findByIdeaIdAndPanelMemberId(UUID ideaId, UUID panelMemberId);

    List<Evaluation> findByIdeaId(UUID ideaId);

    boolean existsByIdeaId(UUID ideaId);

    /**
     * SQL-side aggregation for a single idea (on-read aggregation; U3-NFR-PERF-1).
     * Returns [aggregateScore(avg normalized), feasibilitySum, impactSum, innovationSum, count].
     */
    @Query("""
            select avg(e.normalizedScore), sum(e.feasibility), sum(e.impact),
                   sum(e.innovation), count(e)
            from Evaluation e
            where e.ideaId = :ideaId
            """)
    Object[] aggregateForIdeaRaw(@Param("ideaId") UUID ideaId);

    /**
     * Per-idea aggregate rows for a whole cycle: [ideaId, avgNormalized, count].
     * Used by ranking/leaderboard (U4) with index support on (cycle_id).
     */
    @Query("""
            select e.ideaId, avg(e.normalizedScore), count(e)
            from Evaluation e
            where e.cycleId = :cycleId
            group by e.ideaId
            """)
    List<Object[]> aggregateByCycle(@Param("cycleId") UUID cycleId);
}
