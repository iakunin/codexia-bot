package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Small;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
// @todo #92 collapse all *CronConfig classes into as inner class of *
public class TooSmallCronConfig implements SchedulingConfigurer {

    private final Small tooSmall;

    private final String cronExpression;

    public TooSmallCronConfig(
        @Qualifier("tooSmall") Small tooSmall,
        @Value("${app.cron.bot.too-small:-}") String cronExpression
    ) {
        this.tooSmall = tooSmall;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable tooSmallRunnable() {
        return new Logging(this.tooSmall);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.tooSmallRunnable(),
            this.cronExpression
        );
    }
}
