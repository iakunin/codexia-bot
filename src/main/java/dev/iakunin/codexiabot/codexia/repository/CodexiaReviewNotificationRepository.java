package dev.iakunin.codexiabot.codexia.repository;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodexiaReviewNotificationRepository extends JpaRepository<CodexiaReviewNotification, Long> {

    Stream<CodexiaReviewNotification> findAllByCodexiaReviewOrderByIdDesc(CodexiaReview review);
}
