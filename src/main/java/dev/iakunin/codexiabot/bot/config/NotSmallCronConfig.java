package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Small;
import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.GithubModule;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class NotSmallCronConfig implements SchedulingConfigurer {

    private final Small notSmall;

    private final String cronExpression;

    public NotSmallCronConfig(
        @Qualifier("notSmall") Small notSmall,
        @Value("${app.cron.bot.not-small:-}") String cronExpression
    ) {
        this.notSmall = notSmall;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable notSmallRunnable() {
        return new Logging(this.notSmall);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.notSmallRunnable(),
            this.cronExpression
        );
    }

    @Configuration
    @AllArgsConstructor
    public static class NotSmallConfig {

        private final GithubModule github;

        private final CodexiaModule codexia;

        private final TooSmallResultRepository repository;

        private final dev.iakunin.codexiabot.bot.toosmall.NotSmall bot;

        @Bean
        public Small notSmall() {
            return new Small(
                this.github,
                this.bot,
                this.codexia,
                this.repository
            );
        }
    }
}
