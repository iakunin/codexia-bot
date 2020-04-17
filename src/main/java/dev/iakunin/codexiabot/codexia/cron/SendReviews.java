package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.codexia.service.ReviewSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_={@Autowired})
public final class SendReviews {

    private final CodexiaReviewRepository codexiaReviewRepository;

    private final ReviewSender reviewSender;

    @Scheduled(cron="${app.cron.codexia.send-reviews:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.codexiaReviewRepository
            .findAllWithoutNotifications()
            .forEach(this.reviewSender::send);

        log.info("Exiting from {}", this.getClass().getName());
    }
}
