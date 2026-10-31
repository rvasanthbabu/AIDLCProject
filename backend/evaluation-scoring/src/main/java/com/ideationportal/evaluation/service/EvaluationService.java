package com.ideationportal.evaluation.service;

import com.ideationportal.core.error.DomainException;
import com.ideationportal.evaluation.domain.Evaluation;
import com.ideationportal.evaluation.repository.EvaluationRepository;
import com.ideationportal.idea.service.IdeaService;
import com.ideationportal.identity.domain.CriteriaWeights;
import com.ideationportal.identity.service.CriteriaConfigService;
import com.ideationportal.identity.service.CycleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Panel-member evaluation (Stories 2.2/2.4) with independence rules (2.3).
 * Enforces active assignment, cycle-open, and metric validation (BR-U3-03..08).
 */
@Service
public class EvaluationService {

    private final EvaluationRepository evaluations;
    private final AssignmentService assignmentService;
    private final CycleService cycleService;
    private final CriteriaConfigService criteriaConfig;
    private final IdeaService ideaService;

    public EvaluationService(EvaluationRepository evaluations,
                             AssignmentService assignmentService,
                             CycleService cycleService,
                             CriteriaConfigService criteriaConfig,
                             IdeaService ideaService) {
        this.evaluations = evaluations;
        this.assignmentService = assignmentService;
        this.cycleService = cycleService;
        this.criteriaConfig = criteriaConfig;
        this.ideaService = ideaService;
    }

    @Transactional
    public Evaluation submitOrUpdate(UUID panelMemberId, UUID ideaId, UUID cycleId,
                                     int feasibility, int impact, int innovation, String comment) {
        if (!assignmentService.isActivelyAssigned(ideaId, panelMemberId)) {
            throw DomainException.forbidden("You are not assigned to evaluate this idea.");
        }
        if (!cycleService.isCycleOpen(cycleId)) {
            throw DomainException.conflict("Evaluations can only be submitted or changed while the cycle is open.");
        }
        CriteriaWeights w = criteriaConfig.getWeights();

        Optional<Evaluation> existing = evaluations.findByIdeaIdAndPanelMemberId(ideaId, panelMemberId);
        Evaluation evaluation;
        boolean firstEvaluationForIdea = !evaluations.existsByIdeaId(ideaId);
        if (existing.isPresent()) {
            evaluation = existing.get();
            evaluation.applyScores(feasibility, impact, innovation, comment,
                    w.getFeasibilityWeight(), w.getImpactWeight(), w.getInnovationWeight());
        } else {
            evaluation = new Evaluation(ideaId, panelMemberId, cycleId,
                    feasibility, impact, innovation, comment,
                    w.getFeasibilityWeight(), w.getImpactWeight(), w.getInnovationWeight());
        }
        Evaluation saved = evaluations.save(evaluation);

        // First evaluation makes the idea eligible as SCORED (Q5=B); idempotent if already scored/beyond.
        if (firstEvaluationForIdea) {
            try {
                ideaService.markScored(ideaId);
            } catch (DomainException ignoredIfNotInEvaluation) {
                // Idea may not be in UNDER_EVALUATION (e.g., assignment path differences); ignore illegal transition.
            }
        }
        return saved;
    }

    /**
     * A panel member may read only their own evaluation for an idea before/after submitting;
     * other members' individual evaluations are never exposed here (independence, Q6=B).
     */
    @Transactional(readOnly = true)
    public Optional<Evaluation> getMyEvaluation(UUID panelMemberId, UUID ideaId) {
        if (!assignmentService.isActivelyAssigned(ideaId, panelMemberId)) {
            throw DomainException.forbidden("You are not assigned to evaluate this idea.");
        }
        return evaluations.findByIdeaIdAndPanelMemberId(ideaId, panelMemberId);
    }
}
