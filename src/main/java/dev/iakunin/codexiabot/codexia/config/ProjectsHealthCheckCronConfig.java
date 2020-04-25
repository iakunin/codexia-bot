package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.ProjectsHealthCheck;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class ProjectsHealthCheckCronConfig implements SchedulingConfigurer {

    private final ProjectsHealthCheck projectsHealthCheck;

    private final String cronExpression;

    public ProjectsHealthCheckCronConfig(
        ProjectsHealthCheck projectsHealthCheck,
        @Value("${app.cron.codexia.projects-health-check:-}") String cronExpression
    ) {
        this.projectsHealthCheck = projectsHealthCheck;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable projectsHealthCheckRunnable() {
        return new Logging(this.projectsHealthCheck);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.projectsHealthCheckRunnable(),
            this.cronExpression
        );
    }
}
