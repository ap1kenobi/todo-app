package com.premonition.todo.app.domain.task;

import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;


/**
 * Aggregate created solely for testing
 */
@SuppressWarnings("unused")
public class DummyAggregate extends AbstractAnnotatedAggregateRoot<String> {

    @AggregateIdentifier
    private String id;

}
