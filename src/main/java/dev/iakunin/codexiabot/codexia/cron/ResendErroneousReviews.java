package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.service.ReviewSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_={@Autowired})
public final class ResendErroneousReviews implements Runnable {

    private final CodexiaReviewNotificationRepository repository;

    private final ReviewSender sender;

    public void run() {
        this.repository
            .findAllByLastStatus(CodexiaReviewNotification.Status.ERROR)
            .stream()
            .map(CodexiaReviewNotification::getCodexiaReview)
            .forEach(this.sender::send);
    }
}
