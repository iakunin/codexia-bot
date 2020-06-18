package dev.iakunin.codexiabot.bot.toomanystars;

import dev.iakunin.codexiabot.bot.Bot;
import dev.iakunin.codexiabot.bot.entity.Result;
import dev.iakunin.codexiabot.bot.entity.TooManyStarsResult;
import dev.iakunin.codexiabot.bot.repository.ResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaBadge;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Slf4j
@RequiredArgsConstructor
public class Submitter {

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
        final GithubRepoStat.GithubApi github = (GithubRepoStat.GithubApi) stat.getStat();

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
