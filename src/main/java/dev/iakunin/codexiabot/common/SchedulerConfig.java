package dev.iakunin.codexiabot.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    private final int threadPoolSize;

    public SchedulerConfig(
        @Value("${spring.task.scheduling.pool.size}") int threadPoolSize
    ) {
        this.threadPoolSize = threadPoolSize;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod="shutdown")
    public ScheduledExecutorService taskExecutor() {
        return Executors.newScheduledThreadPool(this.threadPoolSize);
    }
}
