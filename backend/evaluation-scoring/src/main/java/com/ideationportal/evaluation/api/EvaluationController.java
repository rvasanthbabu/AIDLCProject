package com.ideationportal.evaluation.api;

import com.ideationportal.core.error.DomainException;
import com.ideationportal.evaluation.api.dto.EvaluationRequest;
import com.ideationportal.evaluation.api.dto.EvaluationResponse;
import com.ideationportal.evaluation.service.EvaluationService;
import com.ideationportal.identity.api.CurrentUser;
import com.ideationportal.identity.api.SecurityContextHelper;
import com.ideationportal.identity.domain.Cycle;
import com.ideationportal.identity.service.CycleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Panel-member evaluation (Stories 2.1/2.2/2.4). PANEL_MEMBER only; active-assignment and
 * cycle-open enforced in the service. The target cycle is the current open cycle.
 */
@RestController
@RequestMapping("/api/ideas/{ideaId}/evaluation")
@PreAuthorize("hasRole('PANEL_MEMBER')")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final CycleService cycleService;

    public EvaluationController(EvaluationService evaluationService, CycleService cycleService) {
        this.evaluationService = evaluationService;
        this.cycleService = cycleService;
    }

    @PutMapping
    public ResponseEntity<EvaluationResponse> submitOrUpdate(@PathVariable UUID ideaId,
                                                             @Valid @RequestBody EvaluationRequest request) {
        CurrentUser actor = SecurityContextHelper.require();
        Cycle active = cycleService.getActiveCycle()
                .orElseThrow(() -> DomainException.conflict("No open cycle for evaluation."));
        EvaluationResponse response = EvaluationResponse.from(
                evaluationService.submitOrUpdate(actor.userId(), ideaId, active.getId(),
                        request.feasibility(), request.impact(), request.innovation(), request.comment()));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<EvaluationResponse> getMine(@PathVariable UUID ideaId) {
        CurrentUser actor = SecurityContextHelper.require();
        return evaluationService.getMyEvaluation(actor.userId(), ideaId)
                .map(e -> ResponseEntity.ok(EvaluationResponse.from(e)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
