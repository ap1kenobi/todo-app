package com.premonition.cqrs.axon.autoconfigure;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerBeanPostProcessor;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(EventHandler.class)
public class AxonQueryAutoConfiguration {

    @Bean
    public AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor(EventBus eventBus) {
        AnnotationEventListenerBeanPostProcessor processor = new AnnotationEventListenerBeanPostProcessor();
        processor.setEventBus(eventBus);
        return processor;
    }

}
