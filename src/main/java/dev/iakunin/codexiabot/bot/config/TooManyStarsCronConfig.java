package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.TooManyStars;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class TooManyStarsCronConfig implements SchedulingConfigurer {

    private final TooManyStars tooManyStars;

    private final String cronExpression;

    public TooManyStarsCronConfig(
        TooManyStars tooManyStars,
        @Value("${app.cron.bot.too-many-stars:-}") String cronExpression
    ) {
        this.tooManyStars = tooManyStars;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable tooManyStarsRunnable() {
        return new Logging(this.tooManyStars);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.tooManyStarsRunnable(),
            this.cronExpression
        );
    }
}
