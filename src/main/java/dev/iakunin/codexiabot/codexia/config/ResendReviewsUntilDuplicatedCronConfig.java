package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.ResendReviewsUntilDuplicated;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class ResendReviewsUntilDuplicatedCronConfig implements SchedulingConfigurer {

    private final ResendReviewsUntilDuplicated resendReviewsUntilDuplicated;

    private final String cronExpression;

    public ResendReviewsUntilDuplicatedCronConfig(
        ResendReviewsUntilDuplicated resendReviewsUntilDuplicated,
        @Value("${app.cron.codexia.resend-reviews-until-duplicated:-}") String cronExpression
    ) {
        this.resendReviewsUntilDuplicated = resendReviewsUntilDuplicated;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable resendReviewsUntilDuplicatedRunnable() {
        return new Logging(this.resendReviewsUntilDuplicated);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.resendReviewsUntilDuplicatedRunnable(),
            this.cronExpression
        );
    }
}
