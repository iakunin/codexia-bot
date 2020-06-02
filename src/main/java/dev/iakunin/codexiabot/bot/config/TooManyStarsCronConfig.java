package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.TooManyStars;
import dev.iakunin.codexiabot.bot.TooManyStars.Submitter;
import dev.iakunin.codexiabot.bot.repository.TooManyStarsResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.GithubModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class TooManyStarsCronConfig implements SchedulingConfigurer {

    private final TooManyStars runnable;

    private final String expression;

    public TooManyStarsCronConfig(
        final TooManyStars runnable,
        @Value("${app.cron.bot.too-many-stars:-}") final String expression
    ) {
        this.runnable = runnable;
        this.expression = expression;
    }

    @Bean
    public Runnable tooManyStarsRunnable() {
        return new Logging(this.runnable);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.tooManyStarsRunnable(),
            this.expression
        );
    }

    @Configuration
    @RequiredArgsConstructor
    public static class TooManyStarsConfig {

        private final GithubModule github;

        private final CodexiaModule codexia;

        private final TooManyStarsResultRepository repository;

        @Bean
        @Autowired
        public TooManyStars tooManyStars(final Submitter submitter) {
            return new TooManyStars(
                this.github,
                this.repository,
                submitter
            );
        }

        @Bean
        public Submitter tooManyStarsSubmitter() {
            return new Submitter(this.repository, this.codexia);
        }
    }
}
