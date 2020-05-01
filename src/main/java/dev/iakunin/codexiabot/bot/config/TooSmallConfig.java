package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.TooSmall;
import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.github.GithubModule;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TooSmallConfig {

    private final GithubModule github;

    private final CodexiaModule codexia;

    private final TooSmallResultRepository repository;

    private final dev.iakunin.codexiabot.bot.toosmall.TooSmall bot;

    @Bean
    public TooSmall tooSmall() {
        return new TooSmall(
            this.github,
            this.bot,
            this.codexia,
            this.repository
        );
    }
}
