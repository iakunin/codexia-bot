package dev.iakunin.codexiabot.config;

import dev.iakunin.codexiabot.CodexiaBotApplication;
import dev.iakunin.codexiabot.util.WireMockWrapper;
import lombok.SneakyThrows;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CodexiaBotApplication.class)
public class GithubConfig {
    @SneakyThrows
    @Bean
    public GitHub gitHub() {
        return new GitHubBuilder()
            .withEndpoint(
                new WireMockWrapper().baseUrl() + "/github"
            ).build();
    }
}
