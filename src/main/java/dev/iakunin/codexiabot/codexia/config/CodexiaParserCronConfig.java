package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.CodexiaParser;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class CodexiaParserCronConfig implements SchedulingConfigurer {

    private final CodexiaParser codexiaParser;

    private final String cronExpression;

    public CodexiaParserCronConfig(
        CodexiaParser codexiaParser,
        @Value("${app.cron.codexia.codexia-parser:-}") String cronExpression
    ) {
        this.codexiaParser = codexiaParser;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable codexiaParserRunnable() {
        return new Logging(this.codexiaParser);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.codexiaParserRunnable(),
            this.cronExpression
        );
    }
}
