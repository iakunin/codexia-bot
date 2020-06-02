package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Bot;
import dev.iakunin.codexiabot.bot.Found;
import dev.iakunin.codexiabot.bot.found.Reddit;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.common.runnable.Logging;
import dev.iakunin.codexiabot.github.GithubModule;
import lombok.RequiredArgsConstructor;
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
public class FoundOnRedditCronConfig implements SchedulingConfigurer {

    private final Found runnable;

    private final String expression;

    public FoundOnRedditCronConfig(
        @Qualifier("foundOnReddit") final Found runnable,
        @Value("${app.cron.bot.found-on-reddit:-}") final String expression
    ) {
        this.runnable = runnable;
        this.expression = expression;
    }

    @Bean
    public Runnable foundOnRedditRunnable() {
        return new Logging(this.runnable);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.foundOnRedditRunnable(),
            this.expression
        );
    }

    @Configuration
    @RequiredArgsConstructor
    public static class FoundOnRedditConfig {

        private final GithubModule github;

        private final CodexiaModule codexia;

        private final Reddit bot;

        @Bean
        public Found foundOnReddit() {
            return new Found(
                Bot.Type.FOUND_ON_REDDIT,
                this.github,
                this.codexia,
                this.bot
            );
        }
    }
}
