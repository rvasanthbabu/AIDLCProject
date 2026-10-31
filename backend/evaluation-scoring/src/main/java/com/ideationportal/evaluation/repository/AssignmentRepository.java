package com.ideationportal.evaluation.repository;

import com.ideationportal.evaluation.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    Optional<Assignment> findByIdeaIdAndPanelMemberId(UUID ideaId, UUID panelMemberId);

    List<Assignment> findByPanelMemberIdAndCycleIdAndActiveTrue(UUID panelMemberId, UUID cycleId);

    boolean existsByIdeaIdAndActiveTrue(UUID ideaId);
}
