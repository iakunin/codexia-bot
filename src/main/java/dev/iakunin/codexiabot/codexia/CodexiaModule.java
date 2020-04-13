package dev.iakunin.codexiabot.codexia;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.List;
import java.util.Optional;

public interface CodexiaModule {

    // @todo #6 split codexiaModule.sendReview() to codexiaModule.createReview() and codexiaModule.sendReview()
    //  - codexiaModule.createReview() - just a saving to DB.
    //  - codexiaModule.sendReview() - real sending review to Codexia api.
    //  - codexiaModule.sendReview() must be used ONLY inside a cron-job.
    //  - In other code there should be only a codexiaModule.createReview() calls.
    //  - All the codexiaModule.createReview() calls MUST be inside a transaction/
    void sendReview(CodexiaReview review);

    void sendMeta(CodexiaProject codexiaProject, String metaKey, String metaValue);

    Optional<CodexiaProject> findCodexiaProject(GithubRepo repo);

    boolean isReviewExist(CodexiaProject codexiaProject, String author, String reason);

    List<CodexiaReview> findAllReviews(CodexiaProject codexiaProject, String author);
}
