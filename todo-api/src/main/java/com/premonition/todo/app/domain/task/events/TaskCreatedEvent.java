package com.premonition.todo.app.domain.task.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreatedEvent implements TaskEvent {

    private String id;

    private String username;

    private String title;
}
