package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.codexia.service.ReviewSender;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_={@Autowired})
public class ResendErroneousReviews implements Runnable {

    private final CodexiaReviewNotificationRepository notificationRepository;

    private final CodexiaReviewRepository reviewRepository;

    private final ReviewSender sender;

    @Transactional
    public void run() {
        new FaultTolerant(
            this.reviewRepository
                .getAll()
                .flatMap(
                    review -> this.notificationRepository
                        .findAllByCodexiaReviewOrderByIdDesc(review)
                        .limit(1L)
                )
                .filter(notification -> notification.getStatus() == CodexiaReviewNotification.Status.ERROR)
                .map(CodexiaReviewNotification::getCodexiaReview)
                .map(review -> () -> this.sender.send(review)),
            tr -> log.error("Unable to resend item", tr.getCause())
        ).run();
    }
}
