package dev.iakunin.codexiabot.bot.up;

import dev.iakunin.codexiabot.bot.entity.Result;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;

public interface Bot {
    boolean shouldReviewBeSubmitted(GithubRepoStat.GithubApi first, GithubRepoStat.GithubApi last);

    Result createResult(GithubRepoStat stat);

    CodexiaMeta createMeta(CodexiaReview review);

    CodexiaReview createReview(GithubRepoStat first, GithubRepoStat last);
}
