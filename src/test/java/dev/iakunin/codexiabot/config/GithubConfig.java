package dev.iakunin.codexiabot.config;

import dev.iakunin.codexiabot.CodexiaBotApplication;
import dev.iakunin.codexiabot.util.WireMockServer;
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
                WireMockServer.getInstance().baseUrl() + "/github"
            ).build();
    }
}
