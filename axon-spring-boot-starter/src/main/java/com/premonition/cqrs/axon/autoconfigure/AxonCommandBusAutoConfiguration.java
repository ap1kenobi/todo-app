package com.premonition.cqrs.axon.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.disruptor.DisruptorCommandBus;
import org.axonframework.commandhandling.disruptor.DisruptorConfiguration;
import org.axonframework.commandhandling.distributed.DistributedCommandBus;
import org.axonframework.commandhandling.distributed.jgroups.JGroupsConnector;
import org.axonframework.commandhandling.distributed.jgroups.JGroupsConnectorFactoryBean;
import org.axonframework.commandhandling.interceptors.BeanValidationInterceptor;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.EventSourcedAggregateRoot;
import org.axonframework.eventsourcing.GenericAggregateFactory;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventstore.EventStore;
import org.axonframework.repository.Repository;
import org.axonframework.serializer.json.JacksonSerializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.List;

import static java.util.Collections.singletonList;

@EnableConfigurationProperties(AxonCommandBusProperties.class)
public class AxonCommandBusAutoConfiguration implements BeanFactoryAware, AutoCloseable {

    @Autowired
    private AxonCommandBusProperties properties;
    private ConfigurableListableBeanFactory beanFactory;
    private volatile boolean repositoriesRegistered;


    @Bean
    public DisruptorCommandBus localSegment(EventStore eventStore, EventBus eventBus) {
        final DisruptorConfiguration configuration = new DisruptorConfiguration();
        configuration
                .setBufferSize(properties.getBufferSize())
                .setExecutor(properties.getExecutor())
                .setInvokerThreadCount(properties.getInvokerThreadCount())
                .setPublisherThreadCount(properties.getPublisherThreadCount());
        return new DisruptorCommandBus(eventStore, eventBus, configuration);
    }

    @Bean
    public JGroupsConnectorFactoryBean jGroupsConnectorFactoryBean(EventStore eventStore,
                                                                   EventBus eventBus,
                                                                   DisruptorCommandBus localSegment,
                                                                   ObjectMapper mapper) {
        final JGroupsConnectorFactoryBean factoryBean = new JGroupsConnectorFactoryBean();
        factoryBean.setLocalSegment(localSegment);
        factoryBean.setConfiguration(properties.getConnectorConfigurationFile());
        factoryBean.setSerializer(new JacksonSerializer(mapper));
        return factoryBean;
    }

    @Bean
    public CommandBus commandBus(JGroupsConnector connector) {
        DistributedCommandBus commandBus = new DistributedCommandBus(connector);
        commandBus.setCommandDispatchInterceptors(singletonList(new BeanValidationInterceptor()));
        return commandBus;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        registerRepositories();
    }

    private void registerRepositories() {
        if (!repositoriesRegistered && AutoConfigurationPackages.has(beanFactory)) {
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AssignableTypeFilter(AbstractAnnotatedAggregateRoot.class));
            registerRepositories(scanner);
            repositoriesRegistered = true;
        }
    }

    private void registerRepositories(ClassPathScanningCandidateComponentProvider scanner) {
        final List<String> packages = AutoConfigurationPackages.get(beanFactory);
        packages.stream()
                .flatMap(p -> scanner.findCandidateComponents(p).stream())
                .map(BeanDefinition::getBeanClassName)
                .map(this::toClass)
                .forEach(this::registerRepository);
    }

    private <T extends EventSourcedAggregateRoot> void registerRepository(Class<T> aggregateType) {
        final DisruptorCommandBus commandBus = beanFactory.getBean(DisruptorCommandBus.class);
        this.beanFactory.registerSingleton(aggregateType.getSimpleName().toLowerCase() + "Repository",
                create(aggregateType, commandBus));
    }

    private <T extends EventSourcedAggregateRoot> Class<T> toClass(String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void close() throws Exception {
        repositoriesRegistered = false;
    }

    private <T extends EventSourcedAggregateRoot> Repository<T> create(Class<T> aggregateType,
                                                                       DisruptorCommandBus commandBus) {
        return commandBus.createRepository(new GenericAggregateFactory<>(aggregateType));
    }

}
