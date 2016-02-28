package com.premonition.cqrs.axon.autoconfigure;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerBeanPostProcessor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.commandhandling.interceptors.BeanValidationInterceptor;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.eventstore.fs.SimpleEventFileResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Configuration
@ConditionalOnClass(CommandBus.class)
public class AxonCommandAutoConfiguration implements BeanFactoryAware, AutoCloseable {

    private ConfigurableListableBeanFactory beanFactory;
    private volatile boolean repositoriesRegistered;

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
    public CommandGatewayFactoryBean<CommandGateway> commandGatewayFactoryBean(CommandBus commandBus) {
        CommandGatewayFactoryBean<CommandGateway> factory = new CommandGatewayFactoryBean<CommandGateway>();
        factory.setCommandBus(commandBus);
        return factory;
    }

    @Bean
    public EventStore eventStore() {
        return new FileSystemEventStore(new SimpleEventFileResolver(new File("data/eventstore")));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        registerEventSourcingRepositories();
    }

    private void registerEventSourcingRepositories() {
        if (!repositoriesRegistered && AutoConfigurationPackages.has(beanFactory)) {
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AssignableTypeFilter(AbstractAnnotatedAggregateRoot.class));
            createRepositories(scanner);
            repositoriesRegistered = true;
        }
    }

    private void createRepositories(ClassPathScanningCandidateComponentProvider scanner) {
        final List<String> packages = AutoConfigurationPackages.get(beanFactory);
        packages.stream()
                .flatMap(p -> scanner.findCandidateComponents(p).stream())
                .map(BeanDefinition::getBeanClassName)
                .map(this::toClass)
                .forEach(this::createRepository);
    }

    private void createRepository(Class<?> aggregateType) {
        final EventStore eventStore = beanFactory.getBean(EventStore.class);
        final EventBus eventBus = beanFactory.getBean(EventBus.class);
        final EventSourcingRepository repository = new EventSourcingRepository(aggregateType, eventStore);
        repository.setEventBus(eventBus);
        this.beanFactory.registerSingleton(aggregateType.getSimpleName().toLowerCase() + "Repository",
                repository);
    }

    private Class<?> toClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void close() throws Exception {
        repositoriesRegistered = false;
    }

}

