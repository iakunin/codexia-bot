package dev.iakunin.codexiabot.hackernews.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.hackernews.cron.IncrementedParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class IncrementedParserCronConfig implements SchedulingConfigurer {

    private final IncrementedParser incrementedParser;

    private final String cronExpression;

    public IncrementedParserCronConfig(
        IncrementedParser incrementedParser,
        @Value("${app.cron.hackernews.incremented-parser:-}") String cronExpression
    ) {
        this.incrementedParser = incrementedParser;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable incrementedParserRunnable() {
        return new Logging(this.incrementedParser);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.incrementedParserRunnable(),
            this.cronExpression
        );
    }
}
