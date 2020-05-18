package dev.iakunin.codexiabot.github.service;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.repository.GithubRepoStatRepository;
import dev.iakunin.codexiabot.github.sdk.CodetabsClient;
import feign.FeignException;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cactoos.list.ListOf;
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
            this.processException(repo, e);
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

    private void processException(GithubRepo repo, FeignException e) {
        // @todo #93 LinesOfCodeImpl: rewrite via custom Feign exceptions
        final var ignore = new ListOf<>(
            HttpStatus.TOO_MANY_REQUESTS.value(),
            HttpStatus.BAD_REQUEST.value(),
            524
        );
        if (!ignore.contains(e.status())) {
            log.error("Error occurred during getting lines of code", e);
            this.repoStatRepository.save(
                new GithubRepoStat()
                    .setStat(new GithubRepoStat.LinesOfCode())
                    .setGithubRepo(repo)
            );
        }
    }

    @SneakyThrows
    private void sleep(long millis) {
        log.debug("Sleeping...");
        Thread.sleep(millis);
    }
}
