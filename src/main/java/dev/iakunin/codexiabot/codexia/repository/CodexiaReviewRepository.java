package dev.iakunin.codexiabot.codexia.repository;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodexiaReviewRepository extends JpaRepository<CodexiaReview, Long> {

    boolean existsByCodexiaProjectAndAuthorAndReason(
        CodexiaProject project,
        String author,
        String reason
    );

    Stream<CodexiaReview> findAllByCodexiaProjectAndAuthorOrderByIdAsc(
        CodexiaProject project,
        String author
    );

    Optional<CodexiaReview> findFirstByCodexiaProjectAndAuthorOrderByIdDesc(
        CodexiaProject project,
        String author
    );

    @Query(
        "select cr "
        + "from CodexiaReview cr "
        + "left join CodexiaReviewNotification crn "
        + "   on (cr = crn.codexiaReview) "
        + "where crn.id is null "
        + "order by cr.id asc"
    )
    Stream<CodexiaReview> findAllWithoutNotifications();

    @Query("select cr from CodexiaReview cr")
    Stream<CodexiaReview> getAll();
}
