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

@Configuration
public class FoundOnRedditCronConfig implements SchedulingConfigurer {

    private final Found foundOnReddit;

    private final String cronExpression;

    public FoundOnRedditCronConfig(
        @Qualifier("foundOnReddit") Found foundOnReddit,
        @Value("${app.cron.bot.found-on-reddit:-}") String cronExpression
    ) {
        this.foundOnReddit = foundOnReddit;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable foundOnRedditRunnable() {
        return new Logging(this.foundOnReddit);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.foundOnRedditRunnable(),
            this.cronExpression
        );
    }

    @Configuration
    @RequiredArgsConstructor
    public static class FoundOnRedditConfig {

        private final GithubModule githubModule;

        private final CodexiaModule codexiaModule;

        private final Reddit bot;

        @Bean
        public Found foundOnReddit() {
            return new Found(
                Bot.Type.FOUND_ON_REDDIT,
                this.githubModule,
                this.codexiaModule,
                this.bot
            );
        }
    }
}
