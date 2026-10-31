package com.ideationportal.core.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Thin wrapper over Spring's ApplicationEventPublisher so domain modules depend on a
 * stable abstraction (eases future extraction to an external broker, decision Q1=C/Q7=B).
 * Publish within a transaction; subscribers should bind to the after-commit phase.
 */
@Component
public class DomainEventPublisher {

    private final ApplicationEventPublisher delegate;

    public DomainEventPublisher(ApplicationEventPublisher delegate) {
        this.delegate = delegate;
    }

    public void publish(DomainEvent event) {
        delegate.publishEvent(event);
    }
}
