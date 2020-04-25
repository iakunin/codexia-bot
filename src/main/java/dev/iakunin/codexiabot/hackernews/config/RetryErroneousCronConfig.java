package dev.iakunin.codexiabot.hackernews.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.hackernews.cron.RetryErroneous;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class RetryErroneousCronConfig implements SchedulingConfigurer {

    private final RetryErroneous retryErroneous;

    private final String cronExpression;

    public RetryErroneousCronConfig(
        RetryErroneous retryErroneous,
        @Value("${app.cron.hackernews.retry-erroneous:-}") String cronExpression
    ) {
        this.retryErroneous = retryErroneous;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable retryErroneousRunnable() {
        return new Logging(this.retryErroneous);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.retryErroneousRunnable(),
            this.cronExpression
        );
    }
}
