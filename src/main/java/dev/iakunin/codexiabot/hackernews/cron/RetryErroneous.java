package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.factory.HackernewsItemFactory;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import dev.iakunin.codexiabot.hackernews.service.Writer;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RetryErroneous implements Runnable {

    private static final String EMPTY_TYPE = "";

    private final HackernewsItemRepository repository;

    private final Runner runner;

    @Transactional
    public void run() {
        try (var items = this.repository.findAllByType(EMPTY_TYPE)) {
            new FaultTolerant(
                items.map(item -> () -> this.runner.run(item)),
                tr -> log.warn("Exception during RetryErroneous", tr.getCause())
            ).run();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    @Service
    public static class Runner {

        private final HackernewsClient client;

        private final HackernewsItemRepository repo;

        private final Writer writer;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void run(final HackernewsItem entity) {
            HackernewsItemFactory.mutateEntity(
                entity,
                Objects.requireNonNull(
                    this.client.getItem(entity.getExternalId()).getBody()
                )
            );
            this.repo.save(entity);
            this.writer.write(entity);
        }
    }
}
