package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.CodexiaParser;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class CodexiaParserCronConfig implements SchedulingConfigurer {

    private final CodexiaParser parser;

    private final String expression;

    public CodexiaParserCronConfig(
        final CodexiaParser parser,
        @Value("${app.cron.codexia.codexia-parser:-}") final String expression
    ) {
        this.parser = parser;
        this.expression = expression;
    }

    @Bean
    public Runnable codexiaParserRunnable() {
        return new Logging(this.parser);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.codexiaParserRunnable(),
            this.expression
        );
    }
}
