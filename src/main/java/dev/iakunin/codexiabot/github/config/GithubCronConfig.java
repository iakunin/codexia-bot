package dev.iakunin.codexiabot.github.config;

import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.cron.stat.Github;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class GithubCronConfig implements SchedulingConfigurer {

    private final Github github;

    private final String cronExpression;

    public GithubCronConfig(
        Github github,
        @Value("${app.cron.github.stat.github:-}") String cronExpression
    ) {
        this.github = github;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable githubRunnable() {
        return new Logging(this.github);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.githubRunnable(),
            this.cronExpression
        );
    }
}
