package dev.iakunin.codexiabot.hackernews.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.hackernews.cron.RetryErroneous;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class RetryErroneousCronConfig implements SchedulingConfigurer {

    private final RetryErroneous runnable;

    private final String expression;

    public RetryErroneousCronConfig(
        final RetryErroneous runnable,
        @Value("${app.cron.hackernews.retry-erroneous:-}") final String expression
    ) {
        this.runnable = runnable;
        this.expression = expression;
    }

    @Bean
    public Runnable retryErroneousRunnable() {
        return new Logging(this.runnable);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.retryErroneousRunnable(),
            this.expression
        );
    }
}
