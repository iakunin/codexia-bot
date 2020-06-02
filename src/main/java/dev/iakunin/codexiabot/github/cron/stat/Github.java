package dev.iakunin.codexiabot.github.cron.stat;

import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.io.IOException;
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
public class Github implements Runnable {

    private final GithubModule github;

    @Transactional
    public void run() {
        try (var repos = this.github.findAllInCodexia()) {
            new FaultTolerant(
                repos.map(repo -> () -> this.updateStat(repo)),
                tr -> log.error("Unable to update stat", tr.getCause())
            ).run();
        }
    }

    private void updateStat(final GithubRepo repo) {
        try {
            this.github.updateStat(repo);
        } catch (final IOException ex) {
            log.error(
                "Exception during updating stat in Github; githubRepoUuid={}",
                repo.getUuid(),
                ex
            );
        }
    }
}
