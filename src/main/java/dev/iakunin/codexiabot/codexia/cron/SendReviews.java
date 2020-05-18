package dev.iakunin.codexiabot.codexia.cron;

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
public class SendReviews implements Runnable {

    private final CodexiaReviewRepository codexiaReviewRepository;

    private final ReviewSender reviewSender;

    @Transactional
    public void run() {
        try (var reviews = this.codexiaReviewRepository.findAllWithoutNotifications()) {
            new FaultTolerant(
                reviews.map(review -> () -> this.reviewSender.send(review)),
                tr -> log.error("Unable to send review", tr.getCause())
            ).run();
        }
    }
}
