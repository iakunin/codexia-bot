package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Up;
import dev.iakunin.codexiabot.bot.repository.StarsUpResultRepository;
import dev.iakunin.codexiabot.bot.up.Stars;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.GithubModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class StarsUpCronConfig implements SchedulingConfigurer {

    private final Up runnable;

    private final String expression;

    public StarsUpCronConfig(
        @Qualifier("starsUp") final Up runnable,
        @Value("${app.cron.bot.stars-up:-}") final String expression
    ) {
        this.runnable = runnable;
        this.expression = expression;
    }

    @Bean
    public Runnable starsUpRunnable() {
        return new Logging(this.runnable);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.starsUpRunnable(),
            this.expression
        );
    }

    @Configuration
    @RequiredArgsConstructor
    public static class StarsUpConfig {

        private final GithubModule github;

        private final CodexiaModule codexia;

        private final StarsUpResultRepository repository;

        private final Stars bot;

        @Bean
        @Autowired
        public Up starsUp(
            @Qualifier("starsUpSubmitter") final Up.Submitter submitter
        ) {
            return new Up(
                this.github,
                this.repository,
                this.bot,
                submitter
            );
        }

        @Bean
        public Up.Submitter starsUpSubmitter() {
            return new Up.Submitter(this.bot, this.repository, this.codexia);
        }
    }
}
