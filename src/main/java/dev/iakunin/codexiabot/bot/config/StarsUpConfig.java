package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Up;
import dev.iakunin.codexiabot.bot.repository.StarsUpResultRepository;
import dev.iakunin.codexiabot.bot.up.Stars;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.github.GithubModule;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class StarsUpConfig {

    private final GithubModule githubModule;

    private final CodexiaModule codexiaModule;

    private final StarsUpResultRepository starsUpResultRepository;

    private final Stars starsUpBot;

    @Bean
    public Up starsUp() {
        return new Up(
            this.githubModule,
            this.starsUpResultRepository,
            this.starsUpBot,
            this.codexiaModule
        );
    }
}
