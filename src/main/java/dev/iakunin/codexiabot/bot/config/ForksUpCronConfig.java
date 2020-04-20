package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Up;
import dev.iakunin.codexiabot.common.runnable.Logging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@Slf4j
public class ForksUpCronConfig implements SchedulingConfigurer {

    private final Up forksUp;

    private final String cronExpression;

    public ForksUpCronConfig(
        @Qualifier("forksUp") Up forksUp,
        @Value("${app.cron.bot.forks-up:-}") String cronExpression
    ) {
        this.forksUp = forksUp;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable forksUpRunnable() {
        return new Logging(this.forksUp);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.forksUpRunnable(),
            this.cronExpression
        );
    }
}
