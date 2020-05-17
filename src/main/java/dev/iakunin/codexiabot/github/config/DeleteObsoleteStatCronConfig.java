package dev.iakunin.codexiabot.github.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.cron.DeleteObsoleteStat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class DeleteObsoleteStatCronConfig implements SchedulingConfigurer {

    private final DeleteObsoleteStat deleteObsoleteStat;

    private final String cronExpression;

    public DeleteObsoleteStatCronConfig(
        DeleteObsoleteStat deleteObsoleteStat,
        @Value("${app.cron.github.delete-obsolete-stat:-}") String cronExpression
    ) {
        this.deleteObsoleteStat = deleteObsoleteStat;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable deleteObsoleteStatRunnable() {
        return new Logging(this.deleteObsoleteStat);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.deleteObsoleteStatRunnable(),
            this.cronExpression
        );
    }
}
