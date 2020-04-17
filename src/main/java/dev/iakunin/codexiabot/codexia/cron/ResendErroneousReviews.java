package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_={@Autowired})
public final class ResendErroneousReviews {

    private final CodexiaReviewNotificationRepository codexiaReviewNotificationRepository;

    private final CodexiaModule codexiaModule;

    @Scheduled(cron="${app.cron.codexia.resend-erroneous-reviews:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.codexiaReviewNotificationRepository.findAllByLastStatus(CodexiaReviewNotification.Status.ERROR)
            .forEach(
                reviewNotification -> this.codexiaModule.saveReview(reviewNotification.getCodexiaReview())
            );

        log.info("Exiting from {}", this.getClass().getName());
    }
}
