package dev.iakunin.codexiabot.github.config.stat.loc;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.cron.stat.loc.WithoutLoc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class WithoutLocCronConfig implements SchedulingConfigurer {

    private final WithoutLoc linesOfCode;

    private final String cronExpression;

    public WithoutLocCronConfig(
        WithoutLoc linesOfCode,
        @Value("${app.cron.github.stat.loc.without-loc:-}") String cronExpression
    ) {
        this.linesOfCode = linesOfCode;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable statLocWithoutLocRunnable() {
        return new Logging(this.linesOfCode);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.statLocWithoutLocRunnable(),
            this.cronExpression
        );
    }
}
