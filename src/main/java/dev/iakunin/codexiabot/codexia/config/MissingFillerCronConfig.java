package dev.iakunin.codexiabot.codexia.config;

import dev.iakunin.codexiabot.codexia.cron.MissingFiller;
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
public class MissingFillerCronConfig implements SchedulingConfigurer {

    private final MissingFiller missingFiller;

    private final String expression;

    public MissingFillerCronConfig(
        final MissingFiller missingFiller,
        @Value("${app.cron.codexia.missing-filler:-}") final String expression
    ) {
        this.missingFiller = missingFiller;
        this.expression = expression;
    }

    @Bean
    public Runnable missingFillerRunnable() {
        return new Logging(this.missingFiller);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.missingFillerRunnable(),
            this.expression
        );
    }
}
