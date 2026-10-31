package com.ideationportal.identity.service;

import com.ideationportal.core.audit.AuditLogWriter;
import com.ideationportal.core.error.DomainException;
import com.ideationportal.identity.domain.CriteriaWeights;
import com.ideationportal.identity.repository.CriteriaWeightsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Global criteria-weight configuration (Story 7.2). Weights must sum to 100 (BR-U1-20).
 */
@Service
public class CriteriaConfigService {

    private final CriteriaWeightsRepository repository;
    private final AuditLogWriter audit;

    public CriteriaConfigService(CriteriaWeightsRepository repository, AuditLogWriter audit) {
        this.repository = repository;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public CriteriaWeights getWeights() {
        return repository.findById(CriteriaWeights.SINGLETON_ID)
                .orElseThrow(() -> DomainException.notFound("Criteria weights are not configured."));
    }

    @Transactional
    public CriteriaWeights setWeights(UUID actorId, int feasibility, int impact, int innovation) {
        if (!WeightsValidator.isValid(feasibility, impact, innovation)) {
            throw DomainException.validation(
                    "Each weight must be between 0 and 100 and the three must sum to exactly 100.");
        }
        CriteriaWeights weights = repository.findById(CriteriaWeights.SINGLETON_ID)
                .orElseGet(() -> new CriteriaWeights(feasibility, impact, innovation, actorId));
        String before = weights.getFeasibilityWeight() + "/" + weights.getImpactWeight()
                + "/" + weights.getInnovationWeight();
        weights.apply(feasibility, impact, innovation, actorId);
        CriteriaWeights saved = repository.save(weights);
        audit.record(actorId, "WEIGHTS_UPDATED", "criteria_weights",
                before + " -> " + feasibility + "/" + impact + "/" + innovation);
        return saved;
    }
}
