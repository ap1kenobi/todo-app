package com.premonition.todo.app.domain.task.handlers;

import com.premonition.todo.app.domain.task.Task;
import com.premonition.todo.app.domain.task.TaskAlreadyCompletedException;
import com.premonition.todo.app.domain.task.commands.*;
import com.premonition.todo.app.domain.task.events.*;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

public class TaskCommandHandlerTest {

    private static final String TASK_ID = "test-id";
    private static final String TASK_CREATOR = "test-user";
    private static final String TITLE = "Test";
    private FixtureConfiguration<Task> fixture;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(Task.class);
        fixture.registerAnnotatedCommandHandler(new TaskCommandHandler(fixture.getRepository()));
    }

    @Test
    public void shouldCreateTask() throws Exception {
        fixture.given()
                .when(new CreateTaskCommand(TASK_ID, TASK_CREATOR, TITLE))
                .expectEvents(new TaskCreatedEvent(TASK_ID, TASK_CREATOR, TITLE));
    }

    @Test
    public void shouldAllowStarringAnIncompleteTask() throws Exception {
        fixture.given(new TaskCreatedEvent(TASK_ID, TASK_CREATOR, TITLE))
                .when(new StarTaskCommand(TASK_ID))
                .expectEvents(new TaskStarredEvent(TASK_ID));
    }

    @Test
    public void shouldAllowUnstarringAStarredTask() throws Exception {
        fixture.given(new TaskCreatedEvent(TASK_ID, TASK_CREATOR, TITLE), new TaskStarredEvent(TASK_ID))
                .when(new UnstarTaskCommand(TASK_ID))
                .expectEvents(new TaskUnstarredEvent(TASK_ID));
    }

    @Test
    public void shouldAllowRenamingATask() throws Exception {
        final String newTitle = "New Title";
        fixture.given(new TaskCreatedEvent(TASK_ID, TASK_CREATOR, TITLE))
                .when(new ModifyTaskTitleCommand(TASK_ID, newTitle))
                .expectEvents(new TaskTitleModifiedEvent(TASK_ID, newTitle));
    }

    @Test
    public void shouldAllowCompletingATask() throws Exception {
        fixture.given(new TaskCreatedEvent(TASK_ID, TASK_CREATOR, TITLE))
                .when(new CompleteTaskCommand(TASK_ID))
                .expectEvents(new TaskCompletedEvent(TASK_ID));
    }

    @Test
    public void shouldNotAllowStarringACompletedTask() throws Exception {
        fixture.given(new TaskCreatedEvent(TASK_ID, TASK_CREATOR, TITLE),
                new TaskCompletedEvent(TASK_ID))
                .when(new StarTaskCommand(TASK_ID))
                .expectException(TaskAlreadyCompletedException.class);
    }

    @Test
    public void shouldNotAllowUnstarringACompletedTask() throws Exception {
        fixture.given(new TaskCreatedEvent(TASK_ID, TASK_CREATOR, TITLE),
                new TaskCompletedEvent(TASK_ID))
                .when(new UnstarTaskCommand(TASK_ID))
                .expectException(TaskAlreadyCompletedException.class);
    }

    @Test
    public void shouldNotAllowCompletingACompletedTask() throws Exception {
        fixture.given(new TaskCreatedEvent(TASK_ID, TASK_CREATOR, TITLE),
                new TaskCompletedEvent(TASK_ID))
                .when(new CompleteTaskCommand(TASK_ID))
                .expectException(TaskAlreadyCompletedException.class);
    }

    @Test
    public void shouldNotAllowRenamingACompletedTask() throws Exception {
        fixture.given(new TaskCreatedEvent(TASK_ID, TASK_CREATOR, TITLE),
                new TaskCompletedEvent(TASK_ID))
                .when(new ModifyTaskTitleCommand(TASK_ID, "Some title"))
                .expectException(TaskAlreadyCompletedException.class);
    }
}