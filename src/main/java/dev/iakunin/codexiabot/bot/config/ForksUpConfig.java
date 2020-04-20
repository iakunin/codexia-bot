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

    private final GithubModule githubModule;

    private final CodexiaModule codexiaModule;

    private final ForksUpResultRepository forksUpResultRepository;

    private final Forks forksUpBot;

    @Bean
    public Up forksUp() {
        return new Up(
            this.githubModule,
            this.forksUpResultRepository,
            this.forksUpBot,
            this.codexiaModule
        );
    }
}
