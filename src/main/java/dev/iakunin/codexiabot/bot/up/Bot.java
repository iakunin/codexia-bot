package dev.iakunin.codexiabot.bot.up;

import dev.iakunin.codexiabot.bot.entity.Result;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;

public interface Bot {
    boolean shouldSubmit(GithubRepoStat.GithubApi first, GithubRepoStat.GithubApi last);

    Result result(GithubRepoStat stat);

    CodexiaMeta meta(CodexiaReview review);

    CodexiaReview review(GithubRepoStat first, GithubRepoStat last);
}
