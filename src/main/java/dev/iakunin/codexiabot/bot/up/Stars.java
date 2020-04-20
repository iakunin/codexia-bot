package dev.iakunin.codexiabot.bot.up;

import dev.iakunin.codexiabot.bot.entity.Result;
import dev.iakunin.codexiabot.bot.entity.StarsUpResult;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import static dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor_={@Autowired})
public final class Stars implements Bot {

    private final CodexiaModule codexiaModule;

    @Override
    public boolean shouldReviewBeSubmitted(GithubApi first, GithubApi last) {
        final int increase = last.getStars() - first.getStars();

        if (increase < 10) {
            return false;
        }

        return increase >= (first.getStars() * 0.05);
    }

    @Override
    public Result createResult(GithubRepoStat stat) {
        return new StarsUpResult()
            .setGithubRepo(stat.getGithubRepo())
            .setGithubRepoStat(stat);
    }

    @Override
    public CodexiaMeta createMeta(CodexiaReview review) {
        return new CodexiaMeta()
            .setCodexiaProject(review.getCodexiaProject())
            .setKey("stars-count")
            .setValue(
                this.codexiaModule
                    .findAllReviews(review.getCodexiaProject(), review.getAuthor())
                    .stream()
                    .map(CodexiaReview::getReason)
                    .collect(Collectors.joining(","))
            );
    }

    @Override
    public CodexiaReview createReview(GithubRepoStat first, GithubRepoStat last) {
        final GithubApi firstStat = (GithubApi) first.getStat();
        final GithubApi lastStat = (GithubApi) last.getStat();

        return new CodexiaReview()
            .setText(
                String.format(
                    "The repo gained %d stars: from %d (at %s) to %d (at %s). " +
                        "See the stars history [here](https://star-history.t9t.io/#%s).",
                    lastStat.getStars() - firstStat.getStars(),
                    firstStat.getStars(),
                    ZonedDateTime.of(first.getCreatedAt(), ZoneOffset.UTC)
                        .truncatedTo(ChronoUnit.SECONDS),
                    lastStat.getStars(),
                    ZonedDateTime.of(last.getCreatedAt(), ZoneOffset.UTC)
                        .truncatedTo(ChronoUnit.SECONDS),
                    first.getGithubRepo().getFullName()
                )
            )
            .setAuthor(dev.iakunin.codexiabot.bot.Bot.Type.STARS_UP.name())
            .setReason(String.valueOf(lastStat.getStars()))
            .setCodexiaProject(
                this.codexiaModule.getCodexiaProject(last.getGithubRepo())
            );
    }
}
