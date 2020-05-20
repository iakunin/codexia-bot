package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.bot.entity.TooSmallResult;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.stream.Stream;

public interface Bot {

    Stream<GithubRepo> repoStream();

    boolean shouldSubmit(Item item);

    TooSmallResult result(GithubRepoStat stat);

    CodexiaReview review(GithubRepoStat stat, Item item);

    CodexiaMeta meta(CodexiaReview review);

    void badge(CodexiaProject project);
}
