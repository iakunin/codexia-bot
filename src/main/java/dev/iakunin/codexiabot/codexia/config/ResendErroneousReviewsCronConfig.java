package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.ResendErroneousReviews;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class ResendErroneousReviewsCronConfig implements SchedulingConfigurer {

    private final ResendErroneousReviews runnable;

    private final String expression;

    public ResendErroneousReviewsCronConfig(
        final ResendErroneousReviews runnable,
        @Value("${app.cron.codexia.resend-erroneous-reviews:-}") final String expression
    ) {
        this.runnable = runnable;
        this.expression = expression;
    }

    @Bean
    public Runnable resendErroneousReviewsRunnable() {
        return new Logging(this.runnable);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.resendErroneousReviewsRunnable(),
            this.expression
        );
    }
}
