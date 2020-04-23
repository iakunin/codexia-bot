package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Bot;
import dev.iakunin.codexiabot.bot.Found;
import dev.iakunin.codexiabot.bot.found.Reddit;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.github.GithubModule;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class FoundOnRedditConfig {

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
