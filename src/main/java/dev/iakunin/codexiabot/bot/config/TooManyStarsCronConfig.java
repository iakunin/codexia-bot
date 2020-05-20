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

@Configuration
public class TooManyStarsCronConfig implements SchedulingConfigurer {

    private final TooManyStars tooManyStars;

    private final String cronExpression;

    public TooManyStarsCronConfig(
        TooManyStars tooManyStars,
        @Value("${app.cron.bot.too-many-stars:-}") String cronExpression
    ) {
        this.tooManyStars = tooManyStars;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable tooManyStarsRunnable() {
        return new Logging(this.tooManyStars);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.tooManyStarsRunnable(),
            this.cronExpression
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
        public TooManyStars tooManyStars(Submitter submitter) {
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
