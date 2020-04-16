package dev.iakunin.codexiabot.codexia;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.List;
import java.util.Optional;

public interface CodexiaModule {

    // @todo #19 All the codexiaModule.createReview() calls MUST be inside a transaction
    void saveReview(CodexiaReview review);

    // @todo #19 sending meta should also be asynchronous (via cron)
    //  here should be only a saving Meta (as saving Review)
    void sendMeta(CodexiaProject codexiaProject, String metaKey, String metaValue);

    Optional<CodexiaProject> findCodexiaProject(GithubRepo repo);

    boolean isReviewExist(CodexiaProject codexiaProject, String author, String reason);

    List<CodexiaReview> findAllReviews(CodexiaProject codexiaProject, String author);
}
