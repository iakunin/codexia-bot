package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.SendBadges;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SendBadgesCronConfig implements SchedulingConfigurer {

    private final SendBadges sendBadges;

    private final String cronExpression;

    public SendBadgesCronConfig(
        SendBadges sendBadges,
        @Value("${app.cron.codexia.send-badges:-}") String cronExpression
    ) {
        this.sendBadges = sendBadges;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable sendBadgesRunnable() {
        return new Logging(this.sendBadges);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.sendBadgesRunnable(),
            this.cronExpression
        );
    }
}
