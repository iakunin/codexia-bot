package dev.iakunin.codexiabot.github.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.cron.DeleteObsoleteStat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class DeleteObsoleteStatCronConfig implements SchedulingConfigurer {

    private final DeleteObsoleteStat deleteObsoleteStat;

    private final String expression;

    public DeleteObsoleteStatCronConfig(
        final DeleteObsoleteStat deleteObsoleteStat,
        @Value("${app.cron.github.delete-obsolete-stat:-}") final String expression
    ) {
        this.deleteObsoleteStat = deleteObsoleteStat;
        this.expression = expression;
    }

    @Bean
    public Runnable deleteObsoleteStatRunnable() {
        return new Logging(this.deleteObsoleteStat);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.deleteObsoleteStatRunnable(),
            this.expression
        );
    }
}
