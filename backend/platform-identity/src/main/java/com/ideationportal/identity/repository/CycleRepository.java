package com.ideationportal.identity.repository;

import com.ideationportal.identity.domain.Cycle;
import com.ideationportal.identity.domain.CycleState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CycleRepository extends JpaRepository<Cycle, UUID> {

    Optional<Cycle> findFirstByState(CycleState state);

    boolean existsByState(CycleState state);

    List<Cycle> findByStateAndCloseDateLessThanEqual(CycleState state, Instant threshold);
}
