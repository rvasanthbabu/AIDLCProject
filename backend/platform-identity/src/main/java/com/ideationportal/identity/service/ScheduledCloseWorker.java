package com.ideationportal.identity.service;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Periodically auto-closes cycles whose closeDate has passed (BR-U1-17).
 * ShedLock ensures only one instance runs the job across replicas (U1-NFR-REL-3).
 */
@Component
public class ScheduledCloseWorker {

    private static final Logger log = LoggerFactory.getLogger(ScheduledCloseWorker.class);

    private final CycleService cycleService;

    public ScheduledCloseWorker(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    @Scheduled(fixedDelayString = "${ideation.cycle.auto-close-interval-ms:60000}")
    @SchedulerLock(name = "autoCloseDueCycles", lockAtLeastFor = "PT30S", lockAtMostFor = "PT5M")
    public void run() {
        int closed = cycleService.autoCloseDueCycles(Instant.now());
        if (closed > 0) {
            log.info("Auto-closed {} cycle(s) past their close date", closed);
        }
    }
}
