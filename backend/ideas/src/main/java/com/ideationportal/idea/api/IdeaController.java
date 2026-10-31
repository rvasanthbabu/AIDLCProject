package com.ideationportal.idea.api;

import com.ideationportal.idea.api.dto.IdeaRequest;
import com.ideationportal.idea.api.dto.IdeaResponse;
import com.ideationportal.idea.api.dto.IdeaSummaryResponse;
import com.ideationportal.idea.domain.Idea;
import com.ideationportal.idea.service.IdeaDraftData;
import com.ideationportal.idea.service.IdeaService;
import com.ideationportal.identity.api.CurrentUser;
import com.ideationportal.identity.api.SecurityContextHelper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Idea endpoints (Stories 1.1-1.3). SUBMITTER role required (SECURITY-08); ownership
 * enforced in the service layer (prevent IDOR).
 */
@RestController
@RequestMapping("/api/ideas")
@PreAuthorize("hasRole('SUBMITTER')")
public class IdeaController {

    private final IdeaService ideaService;

    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    @PostMapping
    public ResponseEntity<IdeaResponse> createDraft(@Valid @RequestBody IdeaRequest request) {
        CurrentUser actor = SecurityContextHelper.require();
        Idea idea = ideaService.createDraft(actor.userId(), toData(request));
        return ResponseEntity.ok(IdeaResponse.from(idea));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IdeaResponse> updateDraft(@PathVariable UUID id,
                                                    @Valid @RequestBody IdeaRequest request) {
        CurrentUser actor = SecurityContextHelper.require();
        Idea idea = ideaService.updateDraft(actor.userId(), id, toData(request));
        return ResponseEntity.ok(IdeaResponse.from(idea));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<IdeaResponse> submit(@PathVariable UUID id) {
        CurrentUser actor = SecurityContextHelper.require();
        Idea idea = ideaService.submitIdea(actor.userId(), id);
        return ResponseEntity.ok(IdeaResponse.from(idea));
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<IdeaSummaryResponse>> listMine(@RequestParam(required = false) UUID cycleId,
                                                              Pageable pageable) {
        CurrentUser actor = SecurityContextHelper.require();
        Page<IdeaSummaryResponse> page = ideaService
                .listMyIdeas(actor.userId(), cycleId, pageable)
                .map(IdeaSummaryResponse::from);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdeaResponse> getOne(@PathVariable UUID id) {
        CurrentUser actor = SecurityContextHelper.require();
        return ResponseEntity.ok(IdeaResponse.from(ideaService.getMyIdea(actor.userId(), id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        CurrentUser actor = SecurityContextHelper.require();
        ideaService.deleteDraft(actor.userId(), id);
        return ResponseEntity.noContent().build();
    }

    private IdeaDraftData toData(IdeaRequest r) {
        return new IdeaDraftData(r.title(), r.problemStatement(), r.proposedSolution(),
                r.expectedImpact(), r.category());
    }
}
