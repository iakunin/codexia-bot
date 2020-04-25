package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.ResendErroneousReviews;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class ResendErroneousReviewsCronConfig implements SchedulingConfigurer {

    private final ResendErroneousReviews resendErroneousReviews;

    private final String cronExpression;

    public ResendErroneousReviewsCronConfig(
        ResendErroneousReviews resendErroneousReviews,
        @Value("${app.cron.codexia.resend-erroneous-reviews:-}") String cronExpression
    ) {
        this.resendErroneousReviews = resendErroneousReviews;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable resendErroneousReviewsRunnable() {
        return new Logging(this.resendErroneousReviews);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.resendErroneousReviewsRunnable(),
            this.cronExpression
        );
    }
}
