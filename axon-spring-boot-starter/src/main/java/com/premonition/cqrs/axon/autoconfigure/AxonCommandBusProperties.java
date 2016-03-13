package com.premonition.cqrs.axon.autoconfigure;


import org.axonframework.common.AxonThreadFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.Executor;

import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.axonframework.commandhandling.disruptor.DisruptorConfiguration.DEFAULT_BUFFER_SIZE;

@ConfigurationProperties(prefix = "axon.command.bus")
public class AxonCommandBusProperties {
    public static final String DEFAULT_THREAD_POOL_NAME = "command-bus";
    public static final int DEFAULT_COMMAND_BUS_THREAD_COUNT = getRuntime().availableProcessors() * 4;
    public static final int DEFAULT_INVOKER_THREAD_COUNT = getRuntime().availableProcessors();
    public static final int DEFAULT_PUBLISHER_THREAD_COUNT = getRuntime().availableProcessors();
    private String connectorConfigurationFile = "udp.xml";
    private int bufferSize = DEFAULT_BUFFER_SIZE;
    private int executorThreadCount = DEFAULT_COMMAND_BUS_THREAD_COUNT;
    private String threadPoolName = DEFAULT_THREAD_POOL_NAME;
    private int invokerThreadCount = DEFAULT_INVOKER_THREAD_COUNT;
    private int publisherThreadCount = DEFAULT_PUBLISHER_THREAD_COUNT;

    public String getConnectorConfigurationFile() {
        return connectorConfigurationFile;
    }

    public void setConnectorConfigurationFile(String connectorConfigurationFile) {
        this.connectorConfigurationFile = connectorConfigurationFile;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getExecutorThreadCount() {
        return executorThreadCount;
    }

    public void setExecutorThreadCount(int executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
    }

    public String getThreadPoolName() {
        return threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    public Executor getExecutor() {
        return newFixedThreadPool(executorThreadCount, new AxonThreadFactory(threadPoolName));
    }

    public int getInvokerThreadCount() {
        return invokerThreadCount;
    }

    public void setInvokerThreadCount(int invokerThreadCount) {
        this.invokerThreadCount = invokerThreadCount;
    }

    public int getPublisherThreadCount() {
        return publisherThreadCount;
    }

    public void setPublisherThreadCount(int publisherThreadCount) {
        this.publisherThreadCount = publisherThreadCount;
    }
}
