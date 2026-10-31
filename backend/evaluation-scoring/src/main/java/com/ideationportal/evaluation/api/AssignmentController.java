package com.ideationportal.evaluation.api;

import com.ideationportal.evaluation.api.dto.AssignRequest;
import com.ideationportal.evaluation.domain.Assignment;
import com.ideationportal.evaluation.service.AssignmentService;
import com.ideationportal.identity.api.CurrentUser;
import com.ideationportal.identity.api.SecurityContextHelper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Assignment management (Story 3.3). ADMINISTRATOR only (SECURITY-08).
 */
@RestController
@RequestMapping("/api/assignments")
@PreAuthorize("hasRole('ADMINISTRATOR')")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public ResponseEntity<List<Map<String, Object>>> assign(@Valid @RequestBody AssignRequest request) {
        CurrentUser actor = SecurityContextHelper.require();
        List<Assignment> created = assignmentService.assignIdea(
                actor.userId(), request.ideaId(), request.cycleId(), request.panelMemberIds());
        return ResponseEntity.ok(created.stream().map(this::view).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable UUID id) {
        CurrentUser actor = SecurityContextHelper.require();
        assignmentService.removeAssignment(actor.userId(), id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> view(Assignment a) {
        return Map.of(
                "id", a.getId(),
                "ideaId", a.getIdeaId(),
                "panelMemberId", a.getPanelMemberId(),
                "active", a.isActive());
    }
}
