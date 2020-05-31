package dev.iakunin.codexiabot.hackernews.cron.healthcheck;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import dev.iakunin.codexiabot.hackernews.HackernewsModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CodexiaItems implements Runnable {

    private final GithubModule github;

    private final HackernewsModule hackernews;

    @Transactional
    public void run() {
        try (var repos = this.github.findAllInCodexiaAndHackernews()) {
            this.hackernews.healthCheckItems(
                repos
                    .flatMap(this.github::findAllRepoSources)
                    .filter(
                        source -> source.getSource() == GithubModule.Source.HACKERNEWS
                    )
                    .map(GithubRepoSource::getExternalId)
                    .map(Integer::valueOf)
            );
        }
    }
}
