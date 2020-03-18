package dev.iakunin.codexiabot.codexia;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import java.util.List;
import java.util.Optional;

public interface CodexiaModule {

    void sendReview(CodexiaReview review);

    void sendMeta(CodexiaProject codexiaProject, String metaKey, String metaValue);

    Optional<CodexiaProject> findByExternalId(Integer externalId);

    Boolean isReviewExist(CodexiaProject codexiaProject, String author, String reason);

    List<CodexiaReview> findAllReviews(CodexiaProject codexiaProject, String author);
}
