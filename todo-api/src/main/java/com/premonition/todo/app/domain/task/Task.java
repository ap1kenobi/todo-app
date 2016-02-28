package com.premonition.todo.app.domain.task;

import com.premonition.todo.app.domain.task.events.*;
import com.premonition.todo.app.domain.task.commands.CreateTaskCommand;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

import javax.validation.constraints.NotNull;

/**
 * Task
 * @author albert
 */
public class Task extends AbstractAnnotatedAggregateRoot<String> {

	/**
	 * The constant serialVersionUID 
	 */
	private static final long serialVersionUID = -5977984483620451665L;
	
	@AggregateIdentifier
	private String id;
	
	@NotNull
	private boolean completed;
	
	/**
	 * Creates a new Task.
	 * 
	 * @param command create Task
	 */
	public Task(CreateTaskCommand command) {
		apply(new TaskCreatedEvent(command.getId(), command.getUsername(), command.getTitle()));
	}
	
	Task() {
	}

	/**
	 * Completes a Task.
	 *
	 */
	public void complete() {
		apply(new TaskCompletedEvent(id));
	}
	
	/**
	 * Stars a Task.
	 *
	 */
	public void star() {
		apply(new TaskStarredEvent(id));
	}
	
	/**
	 * Unstars a Task.
	 *
	 */
	public void unstar() {
		apply(new TaskUnstarredEvent(id));
	}
	
	/**
	 * Modifies a Task title.
	 *
	 */
	public void rename(String title) {
		assertNotCompleted();
		apply(new TaskTitleModifiedEvent(id, title));
	}
	
	@EventSourcingHandler
	void on(TaskCreatedEvent event) {
		this.id = event.getId();
	}
	
	@EventSourcingHandler
	void on(TaskCompletedEvent event) {
		this.completed = true;
	}
	
	private void assertNotCompleted() {
		if (completed) {
			throw new TaskAlreadyCompletedException("Task [ identifier = " + id + " ] is completed.");
		}		
	}
}
