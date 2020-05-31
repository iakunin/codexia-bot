package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.DeleteObsoleteNotifications;
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
public class DeleteObsoleteNotificationsCronConfig implements SchedulingConfigurer {

    private final DeleteObsoleteNotifications deleteObsolete;

    private final String expression;

    public DeleteObsoleteNotificationsCronConfig(
        final DeleteObsoleteNotifications deleteObsolete,
        @Value("${app.cron.codexia.delete-obsolete-notifications:-}") final String expression
    ) {
        this.deleteObsolete = deleteObsolete;
        this.expression = expression;
    }

    @Bean
    public Runnable deleteObsoleteRunnable() {
        return new Logging(this.deleteObsolete);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.deleteObsoleteRunnable(),
            this.expression
        );
    }
}
