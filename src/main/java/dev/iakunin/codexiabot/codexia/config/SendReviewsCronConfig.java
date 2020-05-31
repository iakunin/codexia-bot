package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.SendReviews;
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
public class SendReviewsCronConfig implements SchedulingConfigurer {

    private final SendReviews sendReviews;

    private final String expression;

    public SendReviewsCronConfig(
        final SendReviews sendReviews,
        @Value("${app.cron.codexia.send-reviews:-}") final String expression
    ) {
        this.sendReviews = sendReviews;
        this.expression = expression;
    }

    @Bean
    public Runnable sendReviewsRunnable() {
        return new Logging(this.sendReviews);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.sendReviewsRunnable(),
            this.expression
        );
    }
}
