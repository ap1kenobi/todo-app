package com.premonition.cqrs.axon.autoconfigure;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass(EventBus.class)
@Import({AxonCommandAutoConfiguration.class, AxonQueryAutoConfiguration.class})
public class AxonAutoConfiguration {

    @Bean
    public EventBus eventBus() {
        return new SimpleEventBus();
    }

}
