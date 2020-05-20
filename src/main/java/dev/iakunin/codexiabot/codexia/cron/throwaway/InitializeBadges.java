package dev.iakunin.codexiabot.codexia.cron.throwaway;

import dev.iakunin.codexiabot.bot.Bot;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaBadge;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitializeBadges implements Runnable {

    private final CodexiaReviewRepository reviewRepository;

    private final CodexiaProjectRepository projectRepository;

    private final Initializer initializer;

    @Transactional
    public void run() {
        this.tooSmall();
        this.tooManyStars();
    }

    private void tooSmall() {
        try (var projects = this.projectRepository.findAllActive()) {
            new FaultTolerant(
                projects
                    .flatMap(
                        project -> this.reviewRepository
                            .findFirstByCodexiaProjectAndAuthorOrderByIdDesc(project, Bot.Type.TOO_SMALL.name())
                            .map(review ->
                                new CodexiaBadge()
                                    .setCodexiaProject(review.getCodexiaProject())
                                    .setBadge("bad")
                                    .setDeletedAt(
                                        review.getText().contains("not small anymore")
                                            ? LocalDateTime.now()
                                            : null
                                    )
                            )
                            .stream()
                    ).map(badge -> () -> this.initializer.init(badge)),
                tr -> log.error("Unable to initialize badge for TOO_SMALL", tr.getCause())
            ).run();
        }
    }

    private void tooManyStars() {
        try (var projects = this.projectRepository.findAllActive()) {
            new FaultTolerant(
                projects
                    .flatMap(
                        project -> this.reviewRepository
                            .findFirstByCodexiaProjectAndAuthorOrderByIdDesc(project, Bot.Type.TOO_MANY_STARS.name())
                            .map(review ->
                                new CodexiaBadge()
                                    .setCodexiaProject(review.getCodexiaProject())
                                    .setBadge("bad")
                            )
                            .stream()
                    ).map(badge -> () -> this.initializer.init(badge)),
                tr -> log.error("Unable to initialize badge for TOO_MANY_STARS", tr.getCause())
            ).run();
        }
    }

    @Service
    @RequiredArgsConstructor
    public static class Initializer {

        private final CodexiaModule codexia;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void init(CodexiaBadge badge) {
            this.codexia.applyBadge(badge);
        }
    }
}
