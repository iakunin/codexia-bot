package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.codexia.service.ReviewSender;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
// @todo #85 Remove this cron after https://github.com/yegor256/codexia/issues/98 is done
public class ResendReviewsUntilDuplicated implements Runnable {

    private final CodexiaReviewNotificationRepository notificationRepository;

    private final CodexiaReviewRepository reviewRepository;

    private final ReviewSender sender;

    @Transactional
    public void run() {
        try (var reviews = this.reviewRepository.getAll()) {
            new FaultTolerant(
                reviews
                    .flatMap(
                        review -> this.notificationRepository
                            .findAllByCodexiaReviewOrderByIdDesc(review)
                            .stream()
                            .limit(1L)
                    )
                    .filter(ntf -> ntf.getStatus() == CodexiaReviewNotification.Status.SUCCESS)
                    .filter(ntf -> ntf.getResponseCode() != CodexiaClient.ReviewStatus.ALREADY_EXISTS.httpStatus())
                    .map(CodexiaReviewNotification::getCodexiaReview)
                    .map(review -> () -> this.sender.send(review)),
                tr -> log.error("Unable to resend item", tr.getCause())
            ).run();
        }
    }
}
