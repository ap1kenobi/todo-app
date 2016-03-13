package com.premonition.todo.app.domain.task.events;

import lombok.Value;

@Value
public class TaskCompletedEvent implements TaskEvent {

    private final String id;
}
