package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Small;
import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
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
public class TooSmallCronConfig implements SchedulingConfigurer {

    private final Small tooSmall;

    private final String cronExpression;

    public TooSmallCronConfig(
        @Qualifier("tooSmall") Small tooSmall,
        @Value("${app.cron.bot.too-small:-}") String cronExpression
    ) {
        this.tooSmall = tooSmall;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable tooSmallRunnable() {
        return new Logging(this.tooSmall);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.tooSmallRunnable(),
            this.cronExpression
        );
    }

    @Configuration
    @AllArgsConstructor
    public static class TooSmallConfig {

        private final GithubModule github;

        private final CodexiaModule codexia;

        private final TooSmallResultRepository repository;

        private final dev.iakunin.codexiabot.bot.toosmall.TooSmall bot;

        @Bean
        @Autowired
        public Small tooSmall(
            @Qualifier("tooSmallSubmitter") Small.Submitter submitter
        ) {
            return new Small(this.github, this.bot, submitter);
        }

        @Bean
        public Small.Submitter tooSmallSubmitter() {
            return new Small.Submitter(this.bot, this.codexia, this.repository);
        }
    }
}
