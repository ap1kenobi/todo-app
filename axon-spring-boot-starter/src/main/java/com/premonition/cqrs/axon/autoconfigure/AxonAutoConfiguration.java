package com.premonition.cqrs.axon.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.eventhandling.*;
import org.axonframework.eventhandling.amqp.spring.ListenerContainerLifecycleManager;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPConsumerConfiguration;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPTerminal;
import org.axonframework.serializer.json.JacksonSerializer;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.springframework.amqp.core.BindingBuilder.bind;

@Configuration
@ConditionalOnClass(EventBus.class)
@Import({AxonCommandAutoConfiguration.class, AxonQueryAutoConfiguration.class})
public class AxonAutoConfiguration {

    public static final String DEFAULT_EVENT_BUS_EXCHANGE_NAME = "Axon.EventBus";
    public static final String DEFAULT_EVENT_BUS_QUEUE_NAME = DEFAULT_EVENT_BUS_EXCHANGE_NAME + ".Queue";

    @Bean
    public EventBus eventBus(EventBusTerminal terminal) {
        final DefaultClusterSelector selector = new DefaultClusterSelector(new SimpleCluster(DEFAULT_EVENT_BUS_QUEUE_NAME));
        return new ClusteringEventBus(selector, terminal);
    }

    @Bean
    public EventBusTerminal eventBusTerminal(ObjectMapper mapper, Exchange exchange) {
        final SpringAMQPTerminal terminal = new SpringAMQPTerminal();
        terminal.setExchange(exchange);
        terminal.setSerializer(new JacksonSerializer(mapper));
        return terminal;
    }

    @Bean
    public FanoutExchange eventBusExchange(AmqpAdmin admin) {
        final FanoutExchange exchange = new FanoutExchange(DEFAULT_EVENT_BUS_EXCHANGE_NAME);
        admin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public Queue eventBusQueue(AmqpAdmin admin, FanoutExchange exchange) {
        final Queue queue = new Queue("Axon.EventBus.Queue");
        admin.declareQueue(queue);
        admin.declareBinding(bind(queue).to(exchange));
        return queue;
    }

    @Bean
    public ListenerContainerLifecycleManager listenerContainerLifecycleManager() {
        final ListenerContainerLifecycleManager lifecycleManager = new ListenerContainerLifecycleManager();
        final SpringAMQPConsumerConfiguration configuration = new SpringAMQPConsumerConfiguration();
        configuration.setQueueName(DEFAULT_EVENT_BUS_QUEUE_NAME);
        lifecycleManager.setDefaultConfiguration(configuration);
        return lifecycleManager;
    }
}
