package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.UpdateProjects;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class UpdateProjectsCronConfig implements SchedulingConfigurer {

    private final UpdateProjects updateProjects;

    private final String expression;

    public UpdateProjectsCronConfig(
        final UpdateProjects updateProjects,
        @Value("${app.cron.codexia.update-projects:-}") final String expression
    ) {
        this.updateProjects = updateProjects;
        this.expression = expression;
    }

    @Bean
    public Runnable updateProjectsRunnable() {
        return new Logging(this.updateProjects);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.updateProjectsRunnable(),
            this.expression
        );
    }
}
