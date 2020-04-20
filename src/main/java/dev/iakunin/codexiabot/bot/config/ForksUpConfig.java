package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.Up;
import dev.iakunin.codexiabot.bot.repository.ForksUpResultRepository;
import dev.iakunin.codexiabot.bot.up.Forks;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.github.GithubModule;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class ForksUpConfig {

    private final GithubModule github;

    private final CodexiaModule codexia;

    private final ForksUpResultRepository repository;

    private final Forks bot;

    @Bean
    public Up forksUp() {
        return new Up(
            this.github,
            this.repository,
            this.bot,
            this.codexia
        );
    }
}
