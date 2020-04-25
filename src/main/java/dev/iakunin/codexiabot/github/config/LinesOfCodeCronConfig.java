package dev.iakunin.codexiabot.github.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.cron.stat.LinesOfCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class LinesOfCodeCronConfig implements SchedulingConfigurer {

    private final LinesOfCode linesOfCode;

    private final String cronExpression;

    public LinesOfCodeCronConfig(
        LinesOfCode linesOfCode,
        @Value("${app.cron.github.stat.lines-of-code:-}") String cronExpression
    ) {
        this.linesOfCode = linesOfCode;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable linesOfCodeRunnable() {
        return new Logging(this.linesOfCode);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.linesOfCodeRunnable(),
            this.cronExpression
        );
    }
}
