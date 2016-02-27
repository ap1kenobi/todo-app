package com.premonition.todo.app.configuration;

import com.premonition.todo.app.domain.task.Task;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerBeanPostProcessor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.commandhandling.interceptors.BeanValidationInterceptor;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerBeanPostProcessor;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.eventstore.fs.SimpleEventFileResolver;
import org.axonframework.repository.Repository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;

@Configuration
public class AxonConfiguration {
	
	@Bean
	public AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor(EventBus eventBus) {
		AnnotationEventListenerBeanPostProcessor processor = new AnnotationEventListenerBeanPostProcessor();
		processor.setEventBus(eventBus);
		return processor;
	}
	
	@Bean
	public AnnotationCommandHandlerBeanPostProcessor annotationCommandHandlerBeanPostProcessor(CommandBus commandBus) {
		AnnotationCommandHandlerBeanPostProcessor processor = new AnnotationCommandHandlerBeanPostProcessor();
		processor.setCommandBus(commandBus);
		return processor;
	}

	@Bean
	public CommandBus commandBus() {
		SimpleCommandBus commandBus = new SimpleCommandBus();
		commandBus.setHandlerInterceptors(Arrays.asList(new BeanValidationInterceptor()));
		return commandBus;
	}

	@Bean
	public EventBus eventBus() {
		return new SimpleEventBus();
	}
	
	@Bean
	public CommandGatewayFactoryBean<CommandGateway> commandGatewayFactoryBean(CommandBus commandBus) {
		CommandGatewayFactoryBean<CommandGateway> factory = new CommandGatewayFactoryBean<CommandGateway>();
		factory.setCommandBus(commandBus);
		return factory;
	}

	@Bean
	public Repository<Task> taskRepository(EventBus eventBus, EventStore eventStore) {
		EventSourcingRepository<Task> repository = new EventSourcingRepository<Task>(Task.class, eventStore);
		repository.setEventBus(eventBus);
		return repository;
	}

	@Bean
	public EventStore eventStore() {
		return new FileSystemEventStore(new SimpleEventFileResolver(new File("data/eventstore")));
	}
}
