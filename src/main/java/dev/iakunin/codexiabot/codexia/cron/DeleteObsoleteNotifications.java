package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public final class DeleteObsoleteNotifications implements Runnable {

    private final CodexiaReviewNotificationRepository repository;

    @Override
    public void run() {
        this.repository
            .findAllObsolete()
            .forEach(
                item -> {
                    log.debug("Deleting item with id={}", item.getId());
                    this.repository.delete(item);
                    log.debug("Successfully deleted item with id={}", item.getId());
                }
            );
    }
}
