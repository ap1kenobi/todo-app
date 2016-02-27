package com.premonition.todo.app.domain.task.commands;

import lombok.Value;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

/**
 * @author albert
 */
@Value
public class CompleteTaskCommand {

	@TargetAggregateIdentifier
	private final String id;
}