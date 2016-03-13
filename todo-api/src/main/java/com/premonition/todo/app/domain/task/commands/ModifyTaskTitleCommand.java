package com.premonition.todo.app.domain.task.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyTaskTitleCommand {

    @TargetAggregateIdentifier
    private String id;

    @NotNull
    private String title;
}

