package dev.iakunin.codexiabot.github.cron.stat;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class Github implements Runnable {

    private final GithubModule githubModule;

    public void run() {
        this.githubModule
            .findAllInCodexia()
            .forEach(this::updateStat);
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
