package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.repository.CodexiaBadgeRepository;
import dev.iakunin.codexiabot.codexia.service.BadgeSender;
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
public class SendBadges implements Runnable {

    private final CodexiaBadgeRepository repository;

    private final BadgeSender sender;

    @Transactional
    public void run() {
        try (var badges = this.repository.getAll()) {
            new FaultTolerant(
                badges.map(badge -> () -> this.sender.send(badge)),
                tr -> log.error("Unable to send badge", tr.getCause())
            ).run();
        }
    }
}
