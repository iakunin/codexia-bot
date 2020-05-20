package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteObsoleteNotifications implements Runnable {

    private final CodexiaReviewRepository reviewRepository;

    private final CodexiaReviewNotificationRepository notificationRepository;

    private final Runner runner;

    @Override
    @Transactional
    public void run() {
        try (var reviews = this.reviewRepository.getAll()) {
            new FaultTolerant(
                reviews
                    .flatMap(
                        review -> this.notificationRepository
                            .findAllByCodexiaReviewOrderByIdDesc(review)
                            .stream()
                            .skip(1L)
                    )
                    .map(item -> () -> this.runner.run(item)),
                tr -> log.error("Unable to delete item", tr.getCause())
            ).run();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    @Service
    public static class Runner {

        private final CodexiaReviewNotificationRepository repository;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void run(CodexiaReviewNotification item) {
            log.debug("Deleting item with id={}", item.getId());
            this.repository.delete(item);
            log.debug("Successfully deleted item with id={}", item.getId());
        }
    }
}
