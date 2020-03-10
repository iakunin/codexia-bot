package dev.iakunin.codexiabot.codexia;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import java.util.Optional;

public interface CodexiaModule {

    void sendReview(CodexiaReview review);

    Optional<CodexiaProject> findByExternalId(Integer externalId);

    Boolean isReviewExist(CodexiaProject codexiaProject, String author, String reason);
}
