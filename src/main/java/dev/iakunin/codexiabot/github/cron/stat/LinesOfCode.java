package dev.iakunin.codexiabot.github.cron.stat;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.repository.GithubRepoRepository;
import dev.iakunin.codexiabot.github.repository.GithubRepoStatRepository;
import dev.iakunin.codexiabot.github.sdk.CodetabsClient;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class LinesOfCode implements Runnable {

    private final GithubRepoRepository githubRepoRepository;

    private final GithubRepoStatRepository githubRepoStatRepository;

    private final CodetabsClient codetabsClient;

    public void run() {
        this.githubRepoRepository.findAllWithoutLinesOfCode().forEach(
            githubRepo -> {
                log.debug("Calculating lines of code for {}", githubRepo);
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
                        log.debug("TOO_MANY_REQUESTS (429) came from codetabs: retrying", e);
                    }
                } finally {
                    log.debug("Sleeping...");
                    sleep(5000);
                }
            }
        );
    }

    @SneakyThrows
    private void sleep(long millis) {
        Thread.sleep(millis);
    }
}
