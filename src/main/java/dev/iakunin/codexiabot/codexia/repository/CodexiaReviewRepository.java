package dev.iakunin.codexiabot.codexia.repository;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodexiaReviewRepository extends JpaRepository<CodexiaReview, Long> {

    boolean existsByCodexiaProjectAndAuthorAndReason(CodexiaProject codexiaProject, String author, String reason);

    List<CodexiaReview> findAllByCodexiaProjectAndAuthor(CodexiaProject codexiaProject, String author);
}
