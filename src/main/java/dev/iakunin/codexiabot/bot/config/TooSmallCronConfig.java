package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Small;
import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
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
public class TooSmallCronConfig implements SchedulingConfigurer {

    private final Small runnable;

    private final String expression;

    public TooSmallCronConfig(
        @Qualifier("tooSmall") final Small runnable,
        @Value("${app.cron.bot.too-small:-}") final String expression
    ) {
        this.runnable = runnable;
        this.expression = expression;
    }

    @Bean
    public Runnable tooSmallRunnable() {
        return new Logging(this.runnable);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.tooSmallRunnable(),
            this.expression
        );
    }

    @Configuration
    @RequiredArgsConstructor
    public static class TooSmallConfig {

        private final GithubModule github;

        private final CodexiaModule codexia;

        private final TooSmallResultRepository repository;

        private final dev.iakunin.codexiabot.bot.toosmall.TooSmall bot;

        @Bean
        @Autowired
        public Small tooSmall(
            @Qualifier("tooSmallSubmitter") final Small.Submitter submitter
        ) {
            return new Small(this.github, this.bot, submitter);
        }

        @Bean
        public Small.Submitter tooSmallSubmitter() {
            return new Small.Submitter(this.bot, this.codexia, this.repository);
        }
    }
}
