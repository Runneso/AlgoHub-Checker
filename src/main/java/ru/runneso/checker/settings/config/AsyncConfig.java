package ru.runneso.checker.settings.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
    @Bean(name = "sqsExecutor")
    public ThreadPoolTaskExecutor sqsExecutor(
            @Value("${sqs.listener.pool-size:10}") int poolSize) {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(poolSize);
        exec.setMaxPoolSize(poolSize);
        exec.setQueueCapacity(poolSize * 2);
        exec.setThreadNamePrefix("sqs-worker-");
        exec.initialize();
        return exec;
    }
}