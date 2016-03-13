package com.premonition.todo.app.notify.task;

import com.premonition.todo.app.domain.task.events.TaskEvent;
import lombok.Value;

@Value
public class TaskEventNotification {

    private String type;

    private TaskEvent data;
}
