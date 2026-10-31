package com.ideationportal.idea.service;

import com.ideationportal.core.audit.AuditLogWriter;
import com.ideationportal.core.error.DomainException;
import com.ideationportal.idea.domain.Idea;
import com.ideationportal.idea.repository.IdeaRepository;
import com.ideationportal.identity.domain.Cycle;
import com.ideationportal.identity.service.CycleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Idea management (Stories 1.1-1.3). Enforces ownership (BR-U2-02/03), draft/submit state
 * rules (BR-U2-05/06/07), and cycle gating via U1 CycleService (BR-U2-06).
 *
 * Authorization note: the SUBMITTER role is enforced at the API layer (@PreAuthorize).
 * Ownership (object-level) is enforced here via submitter-scoped repository lookups.
 */
@Service
public class IdeaService {

    private final IdeaRepository ideas;
    private final CycleService cycleService;
    private final AuditLogWriter audit;

    public IdeaService(IdeaRepository ideas, CycleService cycleService, AuditLogWriter audit) {
        this.ideas = ideas;
        this.cycleService = cycleService;
        this.audit = audit;
    }

    @Transactional
    public Idea createDraft(UUID submitterId, IdeaDraftData data) {
        Idea idea = new Idea(submitterId);
        idea.applyDraftFields(data.title(), data.problemStatement(), data.proposedSolution(),
                data.expectedImpact(), data.category());
        return ideas.save(idea);
    }

    @Transactional
    public Idea updateDraft(UUID submitterId, UUID ideaId, IdeaDraftData data) {
        Idea idea = requireOwned(ideaId, submitterId);
        idea.applyDraftFields(data.title(), data.problemStatement(), data.proposedSolution(),
                data.expectedImpact(), data.category());
        return ideas.save(idea);
    }

    @Transactional
    public Idea submitIdea(UUID submitterId, UUID ideaId) {
        Idea idea = requireOwned(ideaId, submitterId);
        Cycle active = cycleService.getActiveCycle()
                .orElseThrow(() -> DomainException.conflict("No open cycle is available for submission."));
        idea.submit(active.getId(), Instant.now());
        Idea saved = ideas.save(idea);
        audit.record(submitterId, "IDEA_SUBMITTED", "idea:" + saved.getId(), "cycle:" + active.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Idea> listMyIdeas(UUID submitterId, UUID cycleId, Pageable pageable) {
        if (cycleId != null) {
            return ideas.findBySubmitterIdAndCycleId(submitterId, cycleId, pageable);
        }
        return ideas.findBySubmitterId(submitterId, pageable);
    }

    @Transactional(readOnly = true)
    public Idea getMyIdea(UUID submitterId, UUID ideaId) {
        return requireOwned(ideaId, submitterId);
    }

    @Transactional
    public void deleteDraft(UUID submitterId, UUID ideaId) {
        Idea idea = requireOwned(ideaId, submitterId);
        if (!idea.isDraft()) {
            throw DomainException.conflict("Only a draft idea can be deleted.");
        }
        ideas.delete(idea);
    }

    // ---- Cross-unit lifecycle mutators (invoked by U3/U4 through this boundary) ----

    @Transactional
    public void markUnderEvaluation(UUID ideaId) {
        Idea idea = ideas.findById(ideaId).orElseThrow(() -> DomainException.notFound("Idea not found."));
        idea.markUnderEvaluation();
        ideas.save(idea);
    }

    @Transactional
    public void markScored(UUID ideaId) {
        Idea idea = ideas.findById(ideaId).orElseThrow(() -> DomainException.notFound("Idea not found."));
        idea.markScored();
        ideas.save(idea);
    }

    @Transactional
    public void markRecognized(UUID ideaId) {
        Idea idea = ideas.findById(ideaId).orElseThrow(() -> DomainException.notFound("Idea not found."));
        idea.markRecognized();
        ideas.save(idea);
    }

    @Transactional
    public void markRejected(UUID ideaId) {
        Idea idea = ideas.findById(ideaId).orElseThrow(() -> DomainException.notFound("Idea not found."));
        idea.markRejected();
        ideas.save(idea);
    }

    private Idea requireOwned(UUID ideaId, UUID submitterId) {
        // Ownership-scoped lookup prevents IDOR: a non-owner gets NOT_FOUND, not another user's idea.
        return ideas.findByIdAndSubmitterId(ideaId, submitterId)
                .orElseThrow(() -> DomainException.notFound("Idea not found."));
    }
}
