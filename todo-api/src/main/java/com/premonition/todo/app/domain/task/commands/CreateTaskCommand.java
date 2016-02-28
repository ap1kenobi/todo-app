package com.premonition.todo.app.domain.task.commands;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class CreateTaskCommand {

	@NotNull
	private final String id;
	
	@NotNull
	private final String username;
	
	@NotNull
	private final String title;
}
