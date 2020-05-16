package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.DeleteObsoleteNotifications;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class DeleteObsoleteNotificationsCronConfig implements SchedulingConfigurer {

    private final DeleteObsoleteNotifications deleteObsolete;

    private final String cronExpression;

    public DeleteObsoleteNotificationsCronConfig(
        DeleteObsoleteNotifications deleteObsolete,
        @Value("${app.cron.codexia.delete-obsolete-notifications:-}") String cronExpression
    ) {
        this.deleteObsolete = deleteObsolete;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable deleteObsoleteRunnable() {
        return new Logging(this.deleteObsolete);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.deleteObsoleteRunnable(),
            this.cronExpression
        );
    }
}
