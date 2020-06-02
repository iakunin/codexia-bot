package dev.iakunin.codexiabot.github.config.stat.loc;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.cron.stat.loc.WithoutLoc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class WithoutLocCronConfig implements SchedulingConfigurer {

    private final WithoutLoc runnable;

    private final String expression;

    public WithoutLocCronConfig(
        final WithoutLoc runnable,
        @Value("${app.cron.github.stat.loc.without-loc:-}") final String expression
    ) {
        this.runnable = runnable;
        this.expression = expression;
    }

    @Bean
    public Runnable statLocWithoutLocRunnable() {
        return new Logging(this.runnable);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.statLocWithoutLocRunnable(),
            this.expression
        );
    }
}
