package dev.iakunin.codexiabot.github.config.stat.loc;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.cron.stat.loc.Codexia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class CodexiaCronConfig implements SchedulingConfigurer {

    private final Codexia codexia;

    private final String expression;

    public CodexiaCronConfig(
        final Codexia codexia,
        @Value("${app.cron.github.stat.loc.codexia:-}") final String expression
    ) {
        this.codexia = codexia;
        this.expression = expression;
    }

    @Bean
    public Runnable statLocCodexiaRunnable() {
        return new Logging(this.codexia);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.statLocCodexiaRunnable(),
            this.expression
        );
    }
}
