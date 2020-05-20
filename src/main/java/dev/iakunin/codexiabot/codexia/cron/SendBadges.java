package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.repository.CodexiaBadgeRepository;
import dev.iakunin.codexiabot.codexia.service.BadgeSender;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_={@Autowired})
public class SendBadges implements Runnable {

    private final CodexiaBadgeRepository badgeRepository;

    private final BadgeSender sender;

    @Transactional
    public void run() {
        try (var badges = this.badgeRepository.getAll()) {
            new FaultTolerant(
                badges.map(badge -> () -> this.sender.send(badge)),
                tr -> log.error("Unable to send badge", tr.getCause())
            ).run();
        }
    }
}
