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

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Configuration
public class FoundOnHackernewsCronConfig implements SchedulingConfigurer {

    private final Found foundOnHackernews;

    private final String expression;

    public FoundOnHackernewsCronConfig(
        @Qualifier("foundOnHackernews") final Found foundOnHackernews,
        @Value("${app.cron.bot.found-on-hackernews:-}") final String expression
    ) {
        this.foundOnHackernews = foundOnHackernews;
        this.expression = expression;
    }

    @Bean
    public Runnable foundOnHackernewsRunnable() {
        return new Logging(this.foundOnHackernews);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(
            this.foundOnHackernewsRunnable(),
            this.expression
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
