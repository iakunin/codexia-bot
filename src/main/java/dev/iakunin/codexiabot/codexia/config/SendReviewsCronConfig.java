package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.SendReviews;
import dev.iakunin.codexiabot.common.runnable.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SendReviewsCronConfig implements SchedulingConfigurer {

    private final SendReviews sendReviews;

    private final String cronExpression;

    public SendReviewsCronConfig(
        SendReviews sendReviews,
        @Value("${app.cron.codexia.send-reviews:-}") String cronExpression
    ) {
        this.sendReviews = sendReviews;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable sendReviewsRunnable() {
        return new Logging(this.sendReviews);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.sendReviewsRunnable(),
            this.cronExpression
        );
    }
}
