package dev.iakunin.codexiabot.github.config.stat;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.cron.stat.Github;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class GithubCronConfig implements SchedulingConfigurer {

    private final Github github;

    private final String expression;

    public GithubCronConfig(
        final Github github,
        @Value("${app.cron.github.stat.github:-}") final String expression
    ) {
        this.github = github;
        this.expression = expression;
    }

    @Bean
    public Runnable statGithubRunnable() {
        return new Logging(this.github);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.statGithubRunnable(),
            this.expression
        );
    }
}
