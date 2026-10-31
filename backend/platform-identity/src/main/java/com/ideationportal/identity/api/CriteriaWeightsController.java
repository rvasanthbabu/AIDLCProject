package com.ideationportal.identity.api;

import com.ideationportal.identity.api.dto.WeightsRequest;
import com.ideationportal.identity.domain.CriteriaWeights;
import com.ideationportal.identity.service.CriteriaConfigService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Criteria weight configuration (Story 7.2). Read allowed to any authenticated user;
 * updates restricted to ADMINISTRATOR.
 */
@RestController
@RequestMapping("/api/admin/weights")
public class CriteriaWeightsController {

    private final CriteriaConfigService criteriaConfigService;

    public CriteriaWeightsController(CriteriaConfigService criteriaConfigService) {
        this.criteriaConfigService = criteriaConfigService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getWeights() {
        return ResponseEntity.ok(view(criteriaConfigService.getWeights()));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Map<String, Object>> setWeights(@Valid @RequestBody WeightsRequest request) {
        CurrentUser actor = SecurityContextHelper.require();
        CriteriaWeights updated = criteriaConfigService.setWeights(
                actor.userId(), request.feasibility(), request.impact(), request.innovation());
        return ResponseEntity.ok(view(updated));
    }

    private Map<String, Object> view(CriteriaWeights w) {
        return Map.of(
                "feasibility", w.getFeasibilityWeight(),
                "impact", w.getImpactWeight(),
                "innovation", w.getInnovationWeight(),
                "updatedAt", w.getUpdatedAt());
    }
}
