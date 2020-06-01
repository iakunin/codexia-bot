package dev.iakunin.codexiabot.codexia.cron;

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
public class SendReviews implements Runnable {

    private final CodexiaReviewRepository repository;

    private final ReviewSender sender;

    @Transactional
    public void run() {
        try (var reviews = this.repository.findAllWithoutNotifications()) {
            new FaultTolerant(
                reviews.map(review -> () -> this.sender.send(review)),
                tr -> log.error("Unable to send review", tr.getCause())
            ).run();
        }
    }
}
