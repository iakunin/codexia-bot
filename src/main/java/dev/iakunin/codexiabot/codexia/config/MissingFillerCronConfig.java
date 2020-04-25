package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.MissingFiller;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class MissingFillerCronConfig implements SchedulingConfigurer {

    private final MissingFiller missingFiller;

    private final String cronExpression;

    public MissingFillerCronConfig(
        MissingFiller missingFiller,
        @Value("${app.cron.codexia.missing-filler:-}") String cronExpression
    ) {
        this.missingFiller = missingFiller;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable missingFillerRunnable() {
        return new Logging(this.missingFiller);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.missingFillerRunnable(),
            this.cronExpression
        );
    }
}
