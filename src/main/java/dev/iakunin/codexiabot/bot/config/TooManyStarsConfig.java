package dev.iakunin.codexiabot.bot.config;

import dev.iakunin.codexiabot.bot.TooManyStars;
import dev.iakunin.codexiabot.bot.repository.TooManyStarsResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.github.GithubModule;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TooManyStarsConfig {

    private final GithubModule github;

    private final CodexiaModule codexia;

    private final TooManyStarsResultRepository repository;

    @Bean
    public TooManyStars tooManyStars() {
        return new TooManyStars(
            this.github,
            this.repository,
            this.codexia
        );
    }
}
