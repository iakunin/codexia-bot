package dev.iakunin.codexiabot.codexia.repository;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodexiaReviewRepository extends JpaRepository<CodexiaReview, Long> {

    Boolean existsByCodexiaProjectAndAuthorAndReason(CodexiaProject codexiaProject, String author, String reason);
}
