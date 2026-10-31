package com.ideationportal.identity.api;

import com.ideationportal.core.error.DomainException;
import com.ideationportal.identity.api.dto.CreateCycleRequest;
import com.ideationportal.identity.domain.Cycle;
import com.ideationportal.identity.service.CycleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * Cycle management (Story 7.3). Create/open/close restricted to ADMINISTRATOR; active-cycle
 * read allowed to any authenticated user.
 */
@RestController
@RequestMapping("/api/cycles")
public class CycleController {

    private final CycleService cycleService;

    public CycleController(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateCycleRequest request) {
        CurrentUser actor = SecurityContextHelper.require();
        Cycle cycle = cycleService.createCycle(actor.userId(), request.name(), request.openDate(), request.closeDate());
        return ResponseEntity.ok(view(cycle));
    }

    @PostMapping("/{id}/open")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Map<String, Object>> open(@PathVariable UUID id) {
        CurrentUser actor = SecurityContextHelper.require();
        return ResponseEntity.ok(view(cycleService.openCycle(actor.userId(), id)));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Map<String, Object>> close(@PathVariable UUID id) {
        CurrentUser actor = SecurityContextHelper.require();
        return ResponseEntity.ok(view(cycleService.closeCycle(actor.userId(), id)));
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> active() {
        Cycle cycle = cycleService.getActiveCycle()
                .orElseThrow(() -> DomainException.notFound("No active cycle."));
        return ResponseEntity.ok(view(cycle));
    }

    private Map<String, Object> view(Cycle c) {
        return Map.of(
                "id", c.getId(),
                "name", c.getName(),
                "state", c.getState(),
                "openDate", c.getOpenDate(),
                "closeDate", c.getCloseDate());
    }
}
