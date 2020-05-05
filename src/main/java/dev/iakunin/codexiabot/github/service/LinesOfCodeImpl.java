package dev.iakunin.codexiabot.github.service;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.repository.GithubRepoStatRepository;
import dev.iakunin.codexiabot.github.sdk.CodetabsClient;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class LinesOfCodeImpl implements LinesOfCode {

    private final GithubRepoStatRepository repoStatRepository;

    private final CodetabsClient codetabsClient;

    private final Integer delay;

    public LinesOfCodeImpl(
        GithubRepoStatRepository repoStatRepository,
        CodetabsClient codetabsClient,
        @Value("${app.github.service.lines-of-code.delay}") Integer delay
    ) {
        this.repoStatRepository = repoStatRepository;
        this.codetabsClient = codetabsClient;
        this.delay = delay;
    }

    @Override
    public void calculate(GithubRepo repo) {
        log.debug("Calculating lines of code for {}", repo);
        try {
            this.repoStatRepository.save(
                this.createStat(
                    repo,
                    this.codetabsClient
                        .getLinesOfCode(repo.getFullName())
                        .getBody()
                )
            );
        } catch (feign.FeignException e) {
            if (e.status() != HttpStatus.TOO_MANY_REQUESTS.value()) {
                log.error("Error occurred during getting lines of code", e);
                this.repoStatRepository.save(
                    new GithubRepoStat()
                        .setStat(new GithubRepoStat.LinesOfCode())
                        .setGithubRepo(repo)
                );
            } else {
                log.debug("TOO_MANY_REQUESTS (429) came from codetabs: retrying", e);
            }
        } finally {
            this.sleep(this.delay);
        }
    }

    private GithubRepoStat createStat(GithubRepo repo, List<CodetabsClient.Item> itemList) {
        return GithubRepoStat.Factory
            .from(
                Objects.requireNonNull(itemList)
            )
            .setGithubRepo(repo);
    }

    @SneakyThrows
    private void sleep(long millis) {
        log.debug("Sleeping...");
        Thread.sleep(millis);
    }
}
