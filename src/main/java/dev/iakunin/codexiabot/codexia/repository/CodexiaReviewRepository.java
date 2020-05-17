package dev.iakunin.codexiabot.codexia.repository;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodexiaReviewRepository extends JpaRepository<CodexiaReview, Long> {

    boolean existsByCodexiaProjectAndAuthorAndReason(CodexiaProject codexiaProject, String author, String reason);

    List<CodexiaReview> findAllByCodexiaProjectAndAuthorOrderByIdAsc(CodexiaProject codexiaProject, String author);

    @Query(
        "select cr " +
        "from CodexiaReview cr " +
        "left join CodexiaReviewNotification crn " +
        "   on (cr = crn.codexiaReview) " +
        "where crn.id is null " +
        "order by cr.id asc"
    )
    List<CodexiaReview> findAllWithoutNotifications();

    @Query("select cr from CodexiaReview cr")
    Stream<CodexiaReview> getAll();
}
