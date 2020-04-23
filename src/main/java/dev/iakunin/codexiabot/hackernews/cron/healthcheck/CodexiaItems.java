package dev.iakunin.codexiabot.hackernews.cron.healthcheck;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import dev.iakunin.codexiabot.hackernews.HackernewsModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class CodexiaItems implements Runnable {

    private final GithubModule githubModule;

    private final HackernewsModule hackernewsModule;

    public void run() {
        this.hackernewsModule.healthCheckItems(
            this.githubModule.findAllInCodexiaAndHackernews()
                .stream()
                .flatMap(
                    repo -> this.githubModule.findAllRepoSources(repo).stream()
                )
                .filter(
                    source -> source.getSource() == GithubModule.Source.HACKERNEWS
                )
                .map(GithubRepoSource::getExternalId)
                .map(Integer::valueOf)
        );
    }
}
