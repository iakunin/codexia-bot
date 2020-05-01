package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.NotSmall;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class NotSmallCronConfig implements SchedulingConfigurer {

    private final NotSmall notSmall;

    private final String cronExpression;

    public NotSmallCronConfig(
        NotSmall notSmall,
        @Value("${app.cron.bot.not-small:-}") String cronExpression
    ) {
        this.notSmall = notSmall;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable notSmallRunnable() {
        return new Logging(this.notSmall);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.notSmallRunnable(),
            this.cronExpression
        );
    }
}
