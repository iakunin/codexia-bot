package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.entity.Result;
import dev.iakunin.codexiabot.bot.entity.TooManyStarsResult;
import dev.iakunin.codexiabot.bot.repository.ResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaBadge;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
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

    @Slf4j
    @RequiredArgsConstructor
    public static class Submitter {

        private final ResultRepository repository;

        private final CodexiaModule codexia;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void submit(final GithubRepoStat stat) {
            final CodexiaReview review = this.review(stat);
            this.repository.save(this.result(stat));
            this.codexia.saveReview(review);
            this.codexia.sendMeta(this.meta(review));
            this.codexia.applyBadge(
                new CodexiaBadge()
                    .setCodexiaProject(review.getCodexiaProject())
                    .setBadge("bad")
            );
        }

        private CodexiaReview review(final GithubRepoStat stat) {
            final GithubApi github = (GithubApi) stat.getStat();

            return new CodexiaReview()
                .setText(
                    String.format(
                        "The repo gained too many stars: %d.",
                        github.getStars()
                    )
                )
                .setAuthor(Bot.Type.TOO_MANY_STARS.name())
                .setReason(String.valueOf(github.getStars()))
                .setCodexiaProject(
                    this.codexia.getCodexiaProject(stat.getGithubRepo())
                );
        }

        // @todo #10 Get rid of TooManyStarsResult - Review is enough (see FoundOnHackernews)
        private Result result(final GithubRepoStat stat) {
            return new TooManyStarsResult()
                .setGithubRepo(stat.getGithubRepo())
                .setGithubRepoStat(stat);
        }

        private CodexiaMeta meta(final CodexiaReview review) {
            return new CodexiaMeta()
                .setCodexiaProject(review.getCodexiaProject())
                .setKey("too-many-stars")
                .setValue(review.getReason());
        }
    }
}
