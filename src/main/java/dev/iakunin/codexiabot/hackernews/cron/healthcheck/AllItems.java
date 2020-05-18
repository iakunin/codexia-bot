package dev.iakunin.codexiabot.hackernews.cron.healthcheck;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.GithubModule.Source;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import dev.iakunin.codexiabot.hackernews.HackernewsModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public class AllItems implements Runnable {

    private final GithubModule github;

    private final HackernewsModule hackernews;

    @Transactional
    public void run() {
        try (var sources = this.github.findAllRepoSources(Source.HACKERNEWS)) {
            this.hackernews.healthCheckItems(
                sources
                    .map(GithubRepoSource::getExternalId)
                    .map(Integer::valueOf)
            );
        }
    }
}
