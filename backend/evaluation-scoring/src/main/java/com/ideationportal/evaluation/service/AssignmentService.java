package com.ideationportal.evaluation.service;

import com.ideationportal.core.audit.AuditLogWriter;
import com.ideationportal.core.error.DomainException;
import com.ideationportal.core.event.DomainEventPublisher;
import com.ideationportal.evaluation.domain.Assignment;
import com.ideationportal.evaluation.event.IdeaAssignedEvent;
import com.ideationportal.evaluation.repository.AssignmentRepository;
import com.ideationportal.idea.service.IdeaService;
import com.ideationportal.identity.domain.Role;
import com.ideationportal.identity.service.CycleService;
import com.ideationportal.identity.service.UserAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Idea-to-panel assignment (Story 3.3). Admin-only (enforced at API). Verifies panel role,
 * moves idea to UNDER_EVALUATION (via U2), and publishes IdeaAssignedEvent (BR-U3-01/02/10/11).
 */
@Service
public class AssignmentService {

    private final AssignmentRepository assignments;
    private final UserAccountService users;
    private final CycleService cycleService;
    private final IdeaService ideaService;
    private final DomainEventPublisher events;
    private final AuditLogWriter audit;

    public AssignmentService(AssignmentRepository assignments,
                             UserAccountService users,
                             CycleService cycleService,
                             IdeaService ideaService,
                             DomainEventPublisher events,
                             AuditLogWriter audit) {
        this.assignments = assignments;
        this.users = users;
        this.cycleService = cycleService;
        this.ideaService = ideaService;
        this.events = events;
        this.audit = audit;
    }

    @Transactional
    public List<Assignment> assignIdea(UUID actorAdminId, UUID ideaId, UUID cycleId, List<UUID> panelMemberIds) {
        if (!cycleService.isCycleOpen(cycleId)) {
            throw DomainException.conflict("Ideas can only be assigned while the cycle is open.");
        }
        List<Assignment> created = new ArrayList<>();
        List<UUID> notifyTargets = new ArrayList<>();
        for (UUID memberId : panelMemberIds) {
            if (users.getRole(memberId) != Role.PANEL_MEMBER) {
                throw DomainException.validation("Assignee must have the PANEL_MEMBER role.");
            }
            // Idempotent on unique (ideaId, memberId): reactivate if a prior assignment exists.
            Assignment assignment = assignments.findByIdeaIdAndPanelMemberId(ideaId, memberId)
                    .orElseGet(() -> new Assignment(ideaId, memberId, cycleId, actorAdminId));
            if (!assignment.isActive() && assignment.getId() != null) {
                // Reactivation path not modeled with a setter; create a fresh active record only if none active.
                assignment = new Assignment(ideaId, memberId, cycleId, actorAdminId);
            }
            created.add(assignments.save(assignment));
            notifyTargets.add(memberId);
        }
        // First assignment moves the idea into evaluation (idempotent if already there).
        try {
            ideaService.markUnderEvaluation(ideaId);
        } catch (DomainException ignoredIfAlreadyUnderEvaluation) {
            // Already UNDER_EVALUATION or beyond; safe to ignore the illegal-transition signal here.
        }
        audit.record(actorAdminId, "IDEA_ASSIGNED", "idea:" + ideaId, "members=" + panelMemberIds.size());
        events.publish(new IdeaAssignedEvent(ideaId, notifyTargets, cycleId));
        return created;
    }

    @Transactional
    public void removeAssignment(UUID actorAdminId, UUID assignmentId) {
        Assignment assignment = assignments.findById(assignmentId)
                .orElseThrow(() -> DomainException.notFound("Assignment not found."));
        assignment.deactivate(); // Evaluation (if any) is kept and still counts (BR-U3-11).
        assignments.save(assignment);
        audit.record(actorAdminId, "ASSIGNMENT_REMOVED", "assignment:" + assignmentId, null);
    }

    @Transactional(readOnly = true)
    public List<Assignment> listActiveForPanelMember(UUID panelMemberId, UUID cycleId) {
        return assignments.findByPanelMemberIdAndCycleIdAndActiveTrue(panelMemberId, cycleId);
    }

    @Transactional(readOnly = true)
    public boolean isActivelyAssigned(UUID ideaId, UUID panelMemberId) {
        return assignments.findByIdeaIdAndPanelMemberId(ideaId, panelMemberId)
                .map(Assignment::isActive)
                .orElse(false);
    }
}
