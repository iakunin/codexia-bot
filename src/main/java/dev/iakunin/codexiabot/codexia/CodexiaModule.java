package dev.iakunin.codexiabot.codexia;

import dev.iakunin.codexiabot.codexia.entity.CodexiaBadge;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.Optional;
import java.util.stream.Stream;

public interface CodexiaModule {

    // @todo #19 All the codexiaModule.createReview() calls MUST be inside a transaction
    void saveReview(CodexiaReview review);

    // @todo #19 sending meta should also be asynchronous (via cron)
    //  here should be only a saving Meta (as saving Review)
    void sendMeta(CodexiaMeta meta);

    void applyBadge(CodexiaBadge badge);

    Optional<CodexiaProject> findCodexiaProject(GithubRepo repo);

    CodexiaProject getCodexiaProject(GithubRepo repo);

    boolean isReviewExist(CodexiaProject codexiaProject, String author, String reason);

    Stream<CodexiaReview> findAllReviews(CodexiaProject codexiaProject, String author);
}
