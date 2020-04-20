package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.entity.Result;
import dev.iakunin.codexiabot.bot.entity.TooManyStarsResult;
import dev.iakunin.codexiabot.bot.repository.ResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public final class TooManyStars implements Runnable {

    private final GithubModule github;

    private final ResultRepository repository;

    private final Submitter submitter;

    public TooManyStars(
        GithubModule github,
        ResultRepository repository,
        CodexiaModule codexia
    ) {
        this.github = github;
        this.repository = repository;
        this.submitter = new Submitter(repository, codexia);
    }

    public void run() {
        this.github.findAllInCodexia()
            .stream()
            .filter(
                repo -> this.repository.findFirstByGithubRepoOrderByIdDesc(repo).isEmpty()
            )
            .map(this.github::findLastGithubApiStat)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(this::shouldSubmit)
            .forEach(this.submitter::submit);
    }

    private boolean shouldSubmit(GithubRepoStat stat) {
        return ((GithubApi) stat.getStat()).getStars() > 10_000L;
    }

    @Slf4j
    @AllArgsConstructor(onConstructor_={@Autowired})
    private static class Submitter {

        private final ResultRepository repository;

        private final CodexiaModule codexia;

        // @todo #50 TooManyStars: add test case with transaction rollback
        @Transactional
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
                        "The repo gained too many stars: %d (at %s).",
                        apiStat.getStars(),
                        ZonedDateTime.of(stat.getCreatedAt(), ZoneOffset.UTC)
                            .truncatedTo(ChronoUnit.SECONDS)
                    )
                )
                .setAuthor(Bot.Type.TOO_MANY_STARS.name())
                .setReason(String.valueOf(apiStat.getStars()))
                .setCodexiaProject(
                    this.codexia.getCodexiaProject(stat.getGithubRepo())
                );
        }

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
