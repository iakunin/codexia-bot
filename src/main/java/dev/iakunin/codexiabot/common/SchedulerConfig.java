package dev.iakunin.codexiabot.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    private final int size;

    public SchedulerConfig(
        @Value("${spring.task.scheduling.pool.size}") final int size
    ) {
        this.size = size;
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.setScheduler(this.taskExecutor());
    }

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService taskExecutor() {
        return Executors.newScheduledThreadPool(this.size);
    }
}
