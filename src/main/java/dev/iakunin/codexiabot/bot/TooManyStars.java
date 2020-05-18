package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.entity.Result;
import dev.iakunin.codexiabot.bot.entity.TooManyStarsResult;
import dev.iakunin.codexiabot.bot.repository.ResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
public class TooManyStars implements Runnable {

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

    private boolean shouldSubmit(GithubRepoStat stat) {
        if (stat.getStat() == null) {
            return false;
        }

        return ((GithubApi) stat.getStat()).getStars() > 10_000L;
    }

    @Slf4j
    @AllArgsConstructor
    public static class Submitter {

        private final ResultRepository repository;

        private final CodexiaModule codexia;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void submit(GithubRepoStat stat) {
            final CodexiaReview review = this.review(stat);
            this.repository.save(this.result(stat));
            this.codexia.saveReview(review);
            this.codexia.sendMeta(this.meta(review));
        }

        private CodexiaReview review(GithubRepoStat stat) {
            final GithubApi apiStat = (GithubApi) stat.getStat();

            return new CodexiaReview()
                .setText(
                    String.format(
                        "The repo gained too many stars: %d.",
                        apiStat.getStars()
                    )
                )
                .setAuthor(Bot.Type.TOO_MANY_STARS.name())
                .setReason(String.valueOf(apiStat.getStars()))
                .setCodexiaProject(
                    this.codexia.getCodexiaProject(stat.getGithubRepo())
                );
        }

        // @todo #10 Get rid of TooManyStarsResult - Review is enough (see FoundOnHackernews)
        private Result result(GithubRepoStat stat) {
            return new TooManyStarsResult()
                .setGithubRepo(stat.getGithubRepo())
                .setGithubRepoStat(stat);
        }

        private CodexiaMeta meta(CodexiaReview review) {
            return new CodexiaMeta()
                .setCodexiaProject(review.getCodexiaProject())
                .setKey("too-many-stars")
                .setValue(review.getReason());
        }
    }
}
