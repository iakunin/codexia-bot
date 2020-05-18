package dev.iakunin.codexiabot.codexia.repository;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodexiaReviewNotificationRepository extends JpaRepository<CodexiaReviewNotification, Long> {

    List<CodexiaReviewNotification> findAllByCodexiaReviewOrderByIdDesc(CodexiaReview review);
}
