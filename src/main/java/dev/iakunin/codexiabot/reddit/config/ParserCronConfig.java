package dev.iakunin.codexiabot.reddit.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.reddit.cron.Parser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class ParserCronConfig implements SchedulingConfigurer {

    private final Parser parser;

    private final String expression;

    public ParserCronConfig(
        final Parser parser,
        @Value("${app.cron.reddit.parser:-}") final String expression
    ) {
        this.parser = parser;
        this.expression = expression;
    }

    @Bean
    public Runnable redditParserRunnable() {
        return new Logging(this.parser);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.redditParserRunnable(),
            this.expression
        );
    }
}
