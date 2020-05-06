package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Up;
import dev.iakunin.codexiabot.bot.Up.Submitter;
import dev.iakunin.codexiabot.bot.repository.ForksUpResultRepository;
import dev.iakunin.codexiabot.bot.up.Forks;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.GithubModule;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class ForksUpCronConfig implements SchedulingConfigurer {

    private final Up forksUp;

    private final String cronExpression;

    public ForksUpCronConfig(
        @Qualifier("forksUp") Up forksUp,
        @Value("${app.cron.bot.forks-up:-}") String cronExpression
    ) {
        this.forksUp = forksUp;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable forksUpRunnable() {
        return new Logging(this.forksUp);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.forksUpRunnable(),
            this.cronExpression
        );
    }

    @Configuration
    @AllArgsConstructor
    public static class ForksUpConfig {

        private final GithubModule github;

        private final CodexiaModule codexia;

        private final ForksUpResultRepository repository;

        private final Forks bot;

        @Bean
        @Autowired
        public Up forksUp(
            @Qualifier("forksUpSubmitter") Submitter submitter
        ) {
            return new Up(
                this.github,
                this.repository,
                this.bot,
                submitter
            );
        }

        @Bean
        public Submitter forksUpSubmitter() {
            return new Submitter(this.bot, this.repository, this.codexia);
        }
    }
}
