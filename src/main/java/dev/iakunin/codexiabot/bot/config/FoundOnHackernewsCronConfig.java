package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Bot;
import dev.iakunin.codexiabot.bot.Found;
import dev.iakunin.codexiabot.bot.found.Hackernews;
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
public class FoundOnHackernewsCronConfig implements SchedulingConfigurer {

    private final Found foundOnHackernews;

    private final String cronExpression;

    public FoundOnHackernewsCronConfig(
        @Qualifier("foundOnHackernews") Found foundOnHackernews,
        @Value("${app.cron.bot.found-on-hackernews:-}") String cronExpression
    ) {
        this.foundOnHackernews = foundOnHackernews;
        this.cronExpression = cronExpression;
    }

    @Bean
    public Runnable foundOnHackernewsRunnable() {
        return new Logging(this.foundOnHackernews);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(
            this.foundOnHackernewsRunnable(),
            this.cronExpression
        );
    }

    @Configuration
    @RequiredArgsConstructor
    public static class FoundOnHackernewsConfig {

        private final GithubModule githubModule;

        private final CodexiaModule codexiaModule;

        private final Hackernews bot;

        @Bean
        public Found foundOnHackernews() {
            return new Found(
                Bot.Type.FOUND_ON_HACKERNEWS,
                this.githubModule,
                this.codexiaModule,
                this.bot
            );
        }
    }
}
