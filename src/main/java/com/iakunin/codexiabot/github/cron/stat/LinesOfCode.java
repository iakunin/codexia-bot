package com.iakunin.codexiabot.github.cron.stat;

import com.iakunin.codexiabot.github.client.Codetabs;
import com.iakunin.codexiabot.github.entity.GithubRepoStat;
import com.iakunin.codexiabot.github.repository.GithubRepoRepository;
import com.iakunin.codexiabot.github.repository.GithubRepoStatRepository;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class LinesOfCode {

    private GithubRepoRepository githubRepoRepository;
    private GithubRepoStatRepository githubRepoStatRepository;
    private Codetabs codetabs;

    @Scheduled(cron="* * * * * *") // every second
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.githubRepoRepository.findAllWithoutLinesOfCode().forEach(
            githubRepo -> {

                log.info("Calculating lines of code for {}", githubRepo);
                try {
                    this.githubRepoStatRepository.save(
                        GithubRepoStat.Factory.from(
                            Objects.requireNonNull(
                                this.codetabs.getLinesOfCode(githubRepo.getFullName())
                                    .getBody()
                            )
                        ).setGithubRepo(githubRepo)
                    );
                } finally {
                    try {
                        log.info("Sleeping...");
                        Thread.sleep(4500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        );
    }
}
