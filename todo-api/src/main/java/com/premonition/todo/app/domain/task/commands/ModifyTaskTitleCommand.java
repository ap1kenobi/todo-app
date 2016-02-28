package com.premonition.todo.app.domain.task.commands;

import lombok.Value;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

import javax.validation.constraints.NotNull;

@Value
public class ModifyTaskTitleCommand {

	@TargetAggregateIdentifier
	private final String id;

	@NotNull
	private final String title;
}

