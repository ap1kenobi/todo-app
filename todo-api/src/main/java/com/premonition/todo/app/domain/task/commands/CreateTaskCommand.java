package com.premonition.todo.app.domain.task.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskCommand {

    @TargetAggregateIdentifier
    @NotNull
    private String id;

    @NotNull
    private String username;

    @NotNull
    private String title;
}
