package dev.iakunin.codexiabot.codexia.repository;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodexiaReviewNotificationRepository extends JpaRepository<CodexiaReviewNotification, Long> {

    @Query(
        "select distinct m1 " +
        "from CodexiaReviewNotification m1 " +
        "left join CodexiaReviewNotification m2 " +
        "   on (m1.codexiaReview = m2.codexiaReview and m1.id < m2.id) " +
        "where m2.id is null " +
        "and m1.status = ?1"
    )
    Set<CodexiaReviewNotification> findAllByLastStatus(CodexiaReviewNotification.Status status);

    Stream<CodexiaReviewNotification> findAllByCodexiaReviewOrderByIdDesc(CodexiaReview review);

    @Query(
        "select distinct m1 " +
        "from CodexiaReviewNotification m1 " +
        "left join CodexiaReviewNotification m2 " +
        "   on (m1.codexiaReview = m2.codexiaReview and m1.id < m2.id) " +
        "where m2.id is null " +
        "and m1.status = ?1 " +
        "and m1.responseCode <> ?2"
    )
    Set<CodexiaReviewNotification> findAllByLastStatusExcludingResponseCode(
        CodexiaReviewNotification.Status status,
        Integer responseCode
    );
}
