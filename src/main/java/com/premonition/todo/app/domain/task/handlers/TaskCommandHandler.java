package com.premonition.todo.app.domain.task.handlers;

import com.premonition.todo.app.domain.task.Task;
import com.premonition.todo.app.domain.task.commands.*;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskCommandHandler {

    private final Repository<Task> tasks;

    @Autowired
    public TaskCommandHandler(Repository<Task> tasks) {
        this.tasks = tasks;
    }

    @CommandHandler
    void on(CreateTaskCommand command) {
        final Task task = new Task(command);
        tasks.add(task);
    }

    @CommandHandler
    void on(ModifyTaskTitleCommand command) {
        tasks.load(command.getId()).rename(command.getTitle());
    }

    @CommandHandler
    void on(StarTaskCommand command) {
        tasks.load(command.getId()).star();
    }

    @CommandHandler
    void on(UnstarTaskCommand command) {
        tasks.load(command.getId()).unstar();
    }

    @CommandHandler
    void on(CompleteTaskCommand command) {
        tasks.load(command.getId()).complete();
    }

}
