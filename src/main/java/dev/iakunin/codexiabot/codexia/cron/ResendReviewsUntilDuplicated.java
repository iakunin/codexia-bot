package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.codexia.service.ReviewSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_={@Autowired})
// @todo #85 Remove this cron after https://github.com/yegor256/codexia/issues/98 is done
public final class ResendReviewsUntilDuplicated implements Runnable {

    private final CodexiaReviewNotificationRepository codexiaReviewNotificationRepository;

    private final ReviewSender reviewSender;

    public void run() {
        this.codexiaReviewNotificationRepository
            .findAllByLastStatusExcludingResponseCode(
                CodexiaReviewNotification.Status.SUCCESS,
                CodexiaClient.REVIEW_ALREADY_EXISTS_STATUS
            )
            .stream()
            .map(CodexiaReviewNotification::getCodexiaReview)
            .forEach(this.reviewSender::send);
    }
}
