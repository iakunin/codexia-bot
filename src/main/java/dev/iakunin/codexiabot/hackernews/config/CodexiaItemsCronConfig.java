package dev.iakunin.codexiabot.hackernews.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.hackernews.cron.healthcheck.CodexiaItems;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class CodexiaItemsCronConfig implements SchedulingConfigurer {

    private final CodexiaItems codexiaItems;

    private final String expression;

    public CodexiaItemsCronConfig(
        final CodexiaItems codexiaItems,
        @Value("${app.cron.hackernews.health-check.codexia-items:-}") final String expression
    ) {
        this.codexiaItems = codexiaItems;
        this.expression = expression;
    }

    @Bean
    public Runnable codexiaItemsRunnable() {
        return new Logging(this.codexiaItems);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.codexiaItemsRunnable(),
            this.expression
        );
    }
}
