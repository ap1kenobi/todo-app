package com.premonition.todo.app.rest.task;

import com.premonition.todo.app.domain.task.commands.CompleteTaskCommand;
import com.premonition.todo.app.domain.task.commands.CreateTaskCommand;
import com.premonition.todo.app.domain.task.commands.ModifyTaskTitleCommand;
import com.premonition.todo.app.domain.task.commands.StarTaskCommand;
import com.premonition.todo.app.query.task.TaskEntry;
import com.premonition.todo.app.query.task.TaskEntryRepository;
import com.premonition.todo.app.rest.task.requests.CreateTaskRequest;
import com.premonition.todo.app.rest.task.requests.ModifyTitleRequest;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.domain.DefaultIdentifierFactory;
import org.axonframework.domain.IdentifierFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final IdentifierFactory identifierFactory = new DefaultIdentifierFactory();

    @Autowired
    private TaskEntryRepository taskEntryRepository;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private CommandGateway commandGateway;

    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    Page<TaskEntry> findAll(Principal principal,
                            @RequestParam(required = false, defaultValue = "false") boolean completed,
                            Pageable pageable) {
        return taskEntryRepository.findByUsernameAndCompleted(principal.getName(), completed, pageable);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(NO_CONTENT)
    public void createTask(Principal principal, @RequestBody @Valid CreateTaskRequest request) {
        commandGateway.send(new CreateTaskCommand(identifierFactory.generateIdentifier(), principal.getName(), request.getTitle()));
    }

    @RequestMapping(value = "{identifier}/title", method = RequestMethod.POST)
    @ResponseStatus(NO_CONTENT)
    public void createTask(@PathVariable String identifier, @RequestBody @Valid ModifyTitleRequest request) {
        commandGateway.send(new ModifyTaskTitleCommand(identifier, request.getTitle()));
    }

    @RequestMapping(value = "{identifier}/complete", method = RequestMethod.POST)
    @ResponseStatus(NO_CONTENT)
    public void createTask(@PathVariable String identifier) {
        commandGateway.send(new CompleteTaskCommand(identifier));
    }

    @RequestMapping(value = "{identifier}/star", method = RequestMethod.POST)
    @ResponseStatus(NO_CONTENT)
    public void starTask(@PathVariable String identifier) {
        commandGateway.send(new StarTaskCommand(identifier));
    }

    @RequestMapping(value = "{identifier}/unstar", method = RequestMethod.POST)
    @ResponseStatus(NO_CONTENT)
    public void unstarTask(@PathVariable String identifier) {
        throw new RuntimeException("Could not unstar task...");
        //commandGateway.sendAndWait(new UnstarTaskCommand(identifier));
    }

    @ExceptionHandler
    public void handleException(Principal principal, Throwable exception) {
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", exception.getMessage());
    }

}