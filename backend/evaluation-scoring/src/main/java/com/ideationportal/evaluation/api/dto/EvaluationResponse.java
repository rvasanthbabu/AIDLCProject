package com.ideationportal.evaluation.api.dto;

import com.ideationportal.evaluation.domain.Evaluation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Evaluation view returned to its owner. Pure data carrier (supports round-trip PBT).
 */
public record EvaluationResponse(
        UUID id,
        UUID ideaId,
        UUID panelMemberId,
        int feasibility,
        int impact,
        int innovation,
        BigDecimal normalizedScore,
        String comment,
        Instant submittedAt) {

    public static EvaluationResponse from(Evaluation e) {
        return new EvaluationResponse(
                e.getId(), e.getIdeaId(), e.getPanelMemberId(),
                e.getFeasibility(), e.getImpact(), e.getInnovation(),
                e.getNormalizedScore(), e.getComment(), e.getSubmittedAt());
    }
}
