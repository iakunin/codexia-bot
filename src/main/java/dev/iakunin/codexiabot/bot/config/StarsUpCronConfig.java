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
public class StarsUpCronConfig implements SchedulingConfigurer {

    private final Up starsUp;

    private final String cronExpression;

    public StarsUpCronConfig(
        @Qualifier("starsUp") Up starsUp,
        @Value("${app.cron.bot.stars-up:-}") String cronExpression
    ) {
        this.starsUp = starsUp;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable starsUpRunnable() {
        return new Logging(this.starsUp);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.starsUpRunnable(),
            this.cronExpression
        );
    }
}
