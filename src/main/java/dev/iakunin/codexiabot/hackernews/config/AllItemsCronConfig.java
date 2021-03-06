package dev.iakunin.codexiabot.hackernews.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.hackernews.cron.healthcheck.AllItems;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class AllItemsCronConfig implements SchedulingConfigurer {

    private final AllItems runnable;

    private final String expression;

    public AllItemsCronConfig(
        final AllItems runnable,
        @Value("${app.cron.hackernews.items-health-check:-}") final String expression
    ) {
        this.runnable = runnable;
        this.expression = expression;
    }

    @Bean
    public Runnable allItemsRunnable() {
        return new Logging(this.runnable);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.allItemsRunnable(),
            this.expression
        );
    }
}
