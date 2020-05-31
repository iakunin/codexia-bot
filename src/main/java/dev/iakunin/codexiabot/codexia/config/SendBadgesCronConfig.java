package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.SendBadges;
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
public class SendBadgesCronConfig implements SchedulingConfigurer {

    private final SendBadges sendBadges;

    private final String expression;

    public SendBadgesCronConfig(
        final SendBadges sendBadges,
        @Value("${app.cron.codexia.send-badges:-}") final String expression
    ) {
        this.sendBadges = sendBadges;
        this.expression = expression;
    }

    @Bean
    public Runnable sendBadgesRunnable() {
        return new Logging(this.sendBadges);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.sendBadgesRunnable(),
            this.expression
        );
    }
}
