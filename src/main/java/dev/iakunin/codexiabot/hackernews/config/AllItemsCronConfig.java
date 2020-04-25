package dev.iakunin.codexiabot.hackernews.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.hackernews.cron.healthcheck.AllItems;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class AllItemsCronConfig implements SchedulingConfigurer {

    private final AllItems allItems;

    private final String cronExpression;

    public AllItemsCronConfig(
        AllItems allItems,
        @Value("${app.cron.hackernews.items-health-check:-}") String cronExpression
    ) {
        this.allItems = allItems;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable allItemsRunnable() {
        return new Logging(this.allItems);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.allItemsRunnable(),
            this.cronExpression
        );
    }
}
