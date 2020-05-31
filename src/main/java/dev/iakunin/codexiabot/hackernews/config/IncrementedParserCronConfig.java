package dev.iakunin.codexiabot.hackernews.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.hackernews.cron.IncrementedParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class IncrementedParserCronConfig implements SchedulingConfigurer {

    private final IncrementedParser parser;

    private final String expression;

    public IncrementedParserCronConfig(
        final IncrementedParser parser,
        @Value("${app.cron.hackernews.incremented-parser:-}") final String expression
    ) {
        this.parser = parser;
        this.expression = expression;
    }

    @Bean
    public Runnable incrementedParserRunnable() {
        return new Logging(this.parser);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.incrementedParserRunnable(),
            this.expression
        );
    }
}
