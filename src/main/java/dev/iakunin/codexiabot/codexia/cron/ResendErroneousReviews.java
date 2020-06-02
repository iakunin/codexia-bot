package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.codexia.service.ReviewSender;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResendErroneousReviews implements Runnable {

    private final CodexiaReviewNotificationRepository ntifications;

    private final CodexiaReviewRepository reviews;

    private final ReviewSender sender;

    @Transactional
    public void run() {
        try (var all = this.reviews.getAll()) {
            new FaultTolerant(
                all
                    .flatMap(
                        review -> this.ntifications
                            .findAllByCodexiaReviewOrderByIdDesc(review)
                            .stream()
                            .limit(1L)
                    )
                    .filter(notification ->
                        notification.getStatus() == CodexiaReviewNotification.Status.ERROR
                    )
                    .map(CodexiaReviewNotification::getCodexiaReview)
                    .map(review -> () -> this.sender.send(review)),
                tr -> log.error("Unable to resend item", tr.getCause())
            ).run();
        }
    }
}
