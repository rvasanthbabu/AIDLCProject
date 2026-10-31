package com.ideationportal.identity.repository;

import com.ideationportal.identity.domain.CriteriaWeights;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CriteriaWeightsRepository extends JpaRepository<CriteriaWeights, UUID> {
}
