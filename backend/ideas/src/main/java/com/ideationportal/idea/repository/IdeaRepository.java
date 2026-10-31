package com.ideationportal.idea.repository;

import com.ideationportal.idea.domain.Idea;
import com.ideationportal.idea.domain.IdeaState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Parameterized Spring Data queries (SECURITY-05). Ownership-scoped lookups prevent IDOR.
 */
public interface IdeaRepository extends JpaRepository<Idea, UUID> {

    Optional<Idea> findByIdAndSubmitterId(UUID id, UUID submitterId);

    Page<Idea> findBySubmitterId(UUID submitterId, Pageable pageable);

    Page<Idea> findBySubmitterIdAndCycleId(UUID submitterId, UUID cycleId, Pageable pageable);

    List<Idea> findByCycleIdAndState(UUID cycleId, IdeaState state);
}
