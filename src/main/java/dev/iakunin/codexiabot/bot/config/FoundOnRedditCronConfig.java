package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Found;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class FoundOnRedditCronConfig implements SchedulingConfigurer {

    private final Found foundOnReddit;

    private final String cronExpression;

    public FoundOnRedditCronConfig(
        @Qualifier("foundOnReddit") Found foundOnReddit,
        @Value("${app.cron.bot.found-on-reddit:-}") String cronExpression
    ) {
        this.foundOnReddit = foundOnReddit;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable foundOnRedditRunnable() {
        return new Logging(this.foundOnReddit);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.foundOnRedditRunnable(),
            this.cronExpression
        );
    }
}
