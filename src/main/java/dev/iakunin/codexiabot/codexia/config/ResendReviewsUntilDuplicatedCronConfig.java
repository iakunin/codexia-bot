package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.ResendReviewsUntilDuplicated;
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
public class ResendReviewsUntilDuplicatedCronConfig implements SchedulingConfigurer {

    private final ResendReviewsUntilDuplicated resendReviewsUntilDuplicated;

    private final String expression;

    public ResendReviewsUntilDuplicatedCronConfig(
        final ResendReviewsUntilDuplicated resendReviewsUntilDuplicated,
        @Value("${app.cron.codexia.resend-reviews-until-duplicated:-}") final String expression
    ) {
        this.resendReviewsUntilDuplicated = resendReviewsUntilDuplicated;
        this.expression = expression;
    }

    @Bean
    public Runnable resendReviewsUntilDuplicatedRunnable() {
        return new Logging(this.resendReviewsUntilDuplicated);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.resendReviewsUntilDuplicatedRunnable(),
            this.expression
        );
    }
}
