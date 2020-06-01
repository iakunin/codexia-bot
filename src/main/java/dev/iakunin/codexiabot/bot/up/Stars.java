package dev.iakunin.codexiabot.bot.up;

import dev.iakunin.codexiabot.bot.entity.Result;
import dev.iakunin.codexiabot.bot.entity.StarsUpResult;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.common.duration.HumanReadable;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import java.time.Duration;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cactoos.scalar.Ternary;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.UncheckedText;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class Stars implements Bot {

    private final CodexiaModule codexia;

    /**
     * @checkstyle MagicNumber (15 lines)
     */
    @Override
    public boolean shouldSubmit(final GithubApi first, final GithubApi last) {
        final int increase = last.getStars() - first.getStars();

        return new Unchecked<>(
            new Ternary<>(
                increase < 10,
                false,
                increase >= (first.getStars() * 0.05)
            )
        ).value();
    }

    @Override
    public Result result(final GithubRepoStat stat) {
        return new StarsUpResult()
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
                .setKey("stars-count")
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
            .setText(this.reviewText(first, last))
            .setAuthor(dev.iakunin.codexiabot.bot.Bot.Type.STARS_UP.name())
            .setReason(
                String.valueOf(this.stars(last))
            )
            .setCodexiaProject(
                this.codexia.getCodexiaProject(last.getGithubRepo())
            );
    }

    private String reviewText(final GithubRepoStat first, final GithubRepoStat last) {
        return String.format(
            "The repo gained %d stars (from %d to %d) in %s. "
            + "See the stars history [here](https://star-history.t9t.io/#%s).",
            this.stars(last) - this.stars(first),
            this.stars(first),
            this.stars(last),
            new UncheckedText(
                new HumanReadable(
                    Duration.between(
                        first.getCreatedAt(),
                        last.getCreatedAt()
                    )
                )
            ).asString(),
            first.getGithubRepo().getFullName()
        );
    }

    private Integer stars(final GithubRepoStat stat) {
        return ((GithubApi) stat.getStat()).getStars();
    }
}
