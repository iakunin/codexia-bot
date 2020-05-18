package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import dev.iakunin.codexiabot.hackernews.service.Writer;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public class RetryErroneous implements Runnable{

    private static final String EMPTY_TYPE = "";

    private final HackernewsItemRepository hackernewsItemRepository;

    private final Runner runner;

    @Transactional
    public void run() {
        try (var items = this.hackernewsItemRepository.findAllByType(EMPTY_TYPE)) {
            new FaultTolerant(
                items.map(item -> () -> this.runner.run(item)),
                tr -> log.warn("Exception during RetryErroneous", tr.getCause())
            ).run();
        }
    }

    @Slf4j
    @AllArgsConstructor
    @Service
    public static class Runner {

        private final HackernewsClient hackernewsClient;

        private final HackernewsItemRepository hackernewsItemRepository;

        private final Writer writer;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void run(HackernewsItem entity) {
            HackernewsItem.Factory.mutateEntity(
                entity,
                Objects.requireNonNull(
                    this.hackernewsClient.getItem(entity.getExternalId()).getBody()
                )
            );
            this.hackernewsItemRepository.save(entity);
            this.writer.write(entity);
        }
    }
}
