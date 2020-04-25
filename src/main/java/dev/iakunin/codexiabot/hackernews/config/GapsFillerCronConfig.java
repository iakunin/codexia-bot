package dev.iakunin.codexiabot.hackernews.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.hackernews.cron.GapsFiller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class GapsFillerCronConfig implements SchedulingConfigurer {

    private final GapsFiller gapsFiller;

    private final String cronExpression;

    public GapsFillerCronConfig(
        GapsFiller gapsFiller,
        @Value("${app.cron.hackernews.gaps-filler:-}") String cronExpression
    ) {
        this.gapsFiller = gapsFiller;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable gapsFillerRunnable() {
        return new Logging(this.gapsFiller);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.gapsFillerRunnable(),
            this.cronExpression
        );
    }
}
