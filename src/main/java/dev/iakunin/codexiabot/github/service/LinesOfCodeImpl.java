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

    private final GithubRepoStatRepository repository;

    private final CodetabsClient codetabs;

    private final Integer delay;

    public LinesOfCodeImpl(
        final GithubRepoStatRepository repository,
        final CodetabsClient codetabs,
        @Value("${app.github.service.lines-of-code.delay}") final Integer delay
    ) {
        this.repository = repository;
        this.codetabs = codetabs;
        this.delay = delay;
    }

    @Override
    public void calculate(final GithubRepo repo) {
        log.debug("Calculating lines of code for {}", repo);
        try {
            this.repository.save(
                this.createStat(
                    repo,
                    this.codetabs
                        .getLinesOfCode(repo.getFullName())
                        .getBody()
                )
            );
        } catch (final FeignException ex) {
            this.processException(repo, ex);
        } finally {
            this.sleep(this.delay);
        }
    }

    private GithubRepoStat createStat(
        final GithubRepo repo,
        final List<CodetabsClient.Item> items
    ) {
        return GithubRepoStat.Factory
            .from(
                Objects.requireNonNull(items)
            )
            .setGithubRepo(repo);
    }

    private void processException(final GithubRepo repo, final FeignException exception) {
        // @todo #93 LinesOfCodeImpl: rewrite via custom Feign exceptions
        final var ignore = new ListOf<>(
            HttpStatus.TOO_MANY_REQUESTS.value(),
            HttpStatus.BAD_REQUEST.value(),
            524
        );
        if (!ignore.contains(exception.status())) {
            log.error("Error occurred during getting lines of code", exception);
            this.repository.save(
                new GithubRepoStat()
                    .setStat(new GithubRepoStat.LinesOfCode())
                    .setGithubRepo(repo)
            );
        }
    }

    @SneakyThrows
    private void sleep(final long millis) {
        log.debug("Sleeping...");
        Thread.sleep(millis);
    }
}
