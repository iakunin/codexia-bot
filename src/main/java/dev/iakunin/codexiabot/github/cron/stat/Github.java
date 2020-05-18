package dev.iakunin.codexiabot.github.cron.stat;

import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public class Github implements Runnable {

    private final GithubModule githubModule;

    @Transactional
    public void run() {
        try (var repos = this.githubModule.findAllInCodexia()) {
            new FaultTolerant(
                repos.map(repo -> () -> this.updateStat(repo)),
                tr -> log.error("Unable to update stat", tr.getCause())
            ).run();
        }
    }

    private void updateStat(GithubRepo githubRepo) {
        try {
            this.githubModule.updateStat(githubRepo);
        } catch (IOException e) {
            log.error(
                "Exception during updating stat in Github; githubRepoUuid={}",
                githubRepo.getUuid(),
                e
            );
        }
    }
}
