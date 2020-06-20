package dev.iakunin.codexiabot.bot.up;

import dev.iakunin.codexiabot.bot.entity.ForksUpResult;
import dev.iakunin.codexiabot.bot.entity.Result;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.common.duration.HumanReadable;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import java.time.Duration;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cactoos.text.UncheckedText;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class Forks implements Bot {

    private static final int MIN_INCREASE = 10;

    private static final double PERCENTS_THRSHLD = 0.05;

    private final CodexiaModule codexia;

    @Override
    public boolean shouldSubmit(final GithubApi first, final GithubApi last) {
        final int increase = last.getForks() - first.getForks();

        return increase >= MIN_INCREASE && increase >= (first.getForks() * PERCENTS_THRSHLD);
    }

    @Override
    public Result result(final GithubRepoStat stat) {
        return new ForksUpResult()
            .setGithubRepo(stat.getGithubRepo())
            .setGithubRepoStat(stat);
    }

    @Override
    public CodexiaMeta meta(final CodexiaReview review) {
        try (var reviews = this.codexia
            .findAllReviews(review.getCodexiaProject(), review.getAuthor())
        ) {
            return new CodexiaMeta()
                .setCodexiaProject(review.getCodexiaProject())
                .setKey("forks-count")
                .setValue(
                    reviews
                        .map(CodexiaReview::getReason)
                        .collect(Collectors.joining(","))
                );
        }
    }

    @Override
    public CodexiaReview review(final GithubRepoStat first, final GithubRepoStat last) {
        return new CodexiaReview()
            .setText(
                String.format(
                    "The repo gained %d forks (from %d to %d) in %s.",
                    this.forks(last) - this.forks(first),
                    this.forks(first),
                    this.forks(last),
                    new UncheckedText(
                        new HumanReadable(
                            Duration.between(
                                first.getCreatedAt(),
                                last.getCreatedAt()
                            )
                        )
                    ).asString()
                )
            )
            .setAuthor(dev.iakunin.codexiabot.bot.Bot.Type.FORKS_UP.name())
            .setReason(String.valueOf(this.forks(last)))
            .setCodexiaProject(
                this.codexia.getCodexiaProject(last.getGithubRepo())
            );
    }

    private Integer forks(final GithubRepoStat stat) {
        return ((GithubApi) stat.getStat()).getForks();
    }
}
