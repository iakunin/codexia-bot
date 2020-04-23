package dev.iakunin.codexiabot.bot.found;

import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.stream.Stream;

public interface Bot {
    Stream<GithubRepo> repoStream();

    GithubModule.Source source();

    String reviewText(String externalId);

    CodexiaMeta meta(CodexiaReview review);
}
