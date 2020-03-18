package dev.iakunin.codexiabot.github.cron.stat;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.repository.GithubRepoRepository;
import dev.iakunin.codexiabot.github.repository.GithubRepoStatRepository;
import dev.iakunin.codexiabot.github.sdk.CodetabsClient;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class LinesOfCode {

    private GithubRepoRepository githubRepoRepository;
    private GithubRepoStatRepository githubRepoStatRepository;
    private CodetabsClient codetabsClient;

    @Scheduled(cron="${app.cron.github.stat.lines-of-code:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.githubRepoRepository.findAllWithoutLinesOfCode().forEach(
            githubRepo -> {

                log.info("Calculating lines of code for {}", githubRepo);
                try {
                    this.githubRepoStatRepository.save(
                        GithubRepoStat.Factory.from(
                            Objects.requireNonNull(
                                this.codetabsClient.getLinesOfCode(githubRepo.getFullName())
                                    .getBody()
                            )
                        ).setGithubRepo(githubRepo)
                    );
                } catch (feign.FeignException e) {
                    if (e.status() != HttpStatus.TOO_MANY_REQUESTS.value()) {
                        log.error("Error occurred during getting lines of code", e);
                        this.githubRepoStatRepository.save(
                            new GithubRepoStat()
                                .setStat(new GithubRepoStat.LinesOfCode())
                                .setGithubRepo(githubRepo)
                        );
                    } else {
                        log.info("TOO_MANY_REQUESTS (429) came from codetabs: retrying", e);
                    }
                } finally {
                    try {
                        log.info("Sleeping...");
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        );

        log.info("Exiting from {}", this.getClass().getName());
    }
}
