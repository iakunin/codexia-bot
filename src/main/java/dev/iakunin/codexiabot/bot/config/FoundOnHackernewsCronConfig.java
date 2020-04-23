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
public class FoundOnHackernewsCronConfig implements SchedulingConfigurer {

    private final Found foundOnHackernews;

    private final String cronExpression;

    public FoundOnHackernewsCronConfig(
        @Qualifier("foundOnHackernews") Found foundOnHackernews,
        @Value("${app.cron.bot.found-on-hackernews:-}") String cronExpression
    ) {
        this.foundOnHackernews = foundOnHackernews;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable foundOnHackernewsRunnable() {
        return new Logging(this.foundOnHackernews);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.foundOnHackernewsRunnable(),
            this.cronExpression
        );
    }
}
