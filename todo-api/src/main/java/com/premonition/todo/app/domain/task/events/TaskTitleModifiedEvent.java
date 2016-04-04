package com.premonition.todo.app.domain.task.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskTitleModifiedEvent implements TaskEvent {

    private String id;

    private String title;
}
