package dev.iakunin.codexiabot.hackernews.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.hackernews.cron.healthcheck.CodexiaItems;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class CodexiaItemsCronConfig implements SchedulingConfigurer {

    private final CodexiaItems codexiaItems;

    private final String cronExpression;

    public CodexiaItemsCronConfig(
        CodexiaItems codexiaItems,
        @Value("${app.cron.hackernews.health-check.codexia-items:-}") String cronExpression
    ) {
        this.codexiaItems = codexiaItems;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable codexiaItemsRunnable() {
        return new Logging(this.codexiaItems);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.codexiaItemsRunnable(),
            this.cronExpression
        );
    }
}
