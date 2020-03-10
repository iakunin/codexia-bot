package dev.iakunin.codexiabot.codexia.repository;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodexiaReviewNotificationRepository extends JpaRepository<CodexiaReviewNotification, Long> {

    @Query(
        "select m1 " +
        "from CodexiaReviewNotification m1 " +
        "left join CodexiaReviewNotification m2 " +
        "   on (m1.codexiaReview = m2.codexiaReview and m1.id < m2.id) " +
        "where m2.id is null " +
        "and m1.status = ?1"
    )
    Set<CodexiaReviewNotification> findAllByLastStatus(CodexiaReviewNotification.Status status);
}
