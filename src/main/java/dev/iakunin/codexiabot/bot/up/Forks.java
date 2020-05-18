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
import lombok.AllArgsConstructor;
import org.cactoos.text.UncheckedText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor_={@Autowired})
public final class Forks implements Bot {

    private final CodexiaModule codexiaModule;

    @Override
    public boolean shouldSubmit(GithubApi first, GithubApi last) {
        final int increase = last.getForks() - first.getForks();

        if (increase < 10) {
            return false;
        }

        return increase >= (first.getForks() * 0.05);
    }

    @Override
    public Result result(GithubRepoStat stat) {
        return new ForksUpResult()
            .setGithubRepo(stat.getGithubRepo())
            .setGithubRepoStat(stat);
    }

    @Override
    public CodexiaMeta meta(CodexiaReview review) {
        try (var reviews = this.codexiaModule
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
    public CodexiaReview review(GithubRepoStat first, GithubRepoStat last) {
        final GithubApi firstStat = (GithubApi) first.getStat();
        final GithubApi lastStat = (GithubApi) last.getStat();

        return new CodexiaReview()
            .setText(
                String.format(
                    "The repo gained %d forks (from %d to %d) in %s.",
                    lastStat.getForks() - firstStat.getForks(),
                    firstStat.getForks(),
                    lastStat.getForks(),
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
            .setReason(String.valueOf(lastStat.getForks()))
            .setCodexiaProject(
                this.codexiaModule.getCodexiaProject(last.getGithubRepo())
            );
    }
}
