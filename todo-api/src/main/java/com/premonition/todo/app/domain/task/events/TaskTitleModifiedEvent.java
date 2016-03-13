package com.premonition.todo.app.domain.task.events;

import lombok.Value;

@Value
public class TaskTitleModifiedEvent implements TaskEvent {

    private final String id;

    private final String title;
}
