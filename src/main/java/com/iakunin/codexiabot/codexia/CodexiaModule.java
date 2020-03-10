package com.iakunin.codexiabot.codexia;

import com.iakunin.codexiabot.codexia.entity.CodexiaProject;
import com.iakunin.codexiabot.codexia.entity.CodexiaReview;
import java.util.Optional;

public interface CodexiaModule {

    void sendReview(CodexiaReview review);

    Optional<CodexiaProject> findByExternalId(Integer externalId);

    Boolean isReviewExist(CodexiaProject codexiaProject, String author, String reason);
}
