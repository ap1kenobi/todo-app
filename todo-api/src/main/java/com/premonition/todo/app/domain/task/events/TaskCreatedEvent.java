package com.premonition.todo.app.domain.task.events;

import lombok.Value;

@Value
public class TaskCreatedEvent implements TaskEvent {

	private final String id;
	
	private final String username;
	
	private final String title;	
}
