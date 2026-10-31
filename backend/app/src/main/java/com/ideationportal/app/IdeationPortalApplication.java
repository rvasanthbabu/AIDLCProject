package com.ideationportal.app;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application shell for the Ideation Portal modular monolith. Scans the shared common-core
 * and all feature modules under the com.ideationportal base package.
 */
@SpringBootApplication(scanBasePackages = "com.ideationportal")
@EntityScan(basePackages = "com.ideationportal")
@EnableJpaRepositories(basePackages = "com.ideationportal")
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT5M")
public class IdeationPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdeationPortalApplication.class, args);
    }
}
