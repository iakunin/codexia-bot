package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.repository.ResultRepository;
import dev.iakunin.codexiabot.bot.toomanystars.Submitter;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Slf4j
@RequiredArgsConstructor
public class TooManyStars implements Runnable {

    private static final long THRESHOLD = 10_000L;

    private final GithubModule github;

    private final ResultRepository repository;

    private final Submitter submitter;

    @Transactional
    public void run() {
        try (var repos = this.github.findAllInCodexia()) {
            new FaultTolerant(
                repos
                    .filter(
                        repo -> this.repository.findFirstByGithubRepoOrderByIdDesc(repo).isEmpty()
                    )
                    .map(this.github::findLastGithubApiStat)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(this::shouldSubmit)
                    .map(stat -> () -> this.submitter.submit(stat)),
                tr -> log.error("Unable to submit review", tr.getCause())
            ).run();
        }
    }

    private boolean shouldSubmit(final GithubRepoStat stat) {
        return Optional.ofNullable(stat.getStat())
            .map(st -> (GithubApi) st)
            .map(st -> st.getStars() > THRESHOLD)
            .orElse(false);
    }
}
