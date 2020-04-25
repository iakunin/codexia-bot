package dev.iakunin.codexiabot.reddit.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.reddit.cron.Parser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class ParserCronConfig implements SchedulingConfigurer {

    private final Parser parser;

    private final String cronExpression;

    public ParserCronConfig(
        Parser parser,
        @Value("${app.cron.reddit.parser:-}") String cronExpression
    ) {
        this.parser = parser;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable redditParserRunnable() {
        return new Logging(this.parser);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.redditParserRunnable(),
            this.cronExpression
        );
    }
}
