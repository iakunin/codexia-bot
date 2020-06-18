package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.hackernews.factory.HackernewsItemFactory;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import dev.iakunin.codexiabot.hackernews.service.Writer;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GapsFiller implements Runnable {

    private final HackernewsItemRepository repository;

    private final HackernewsClient hackernews;

    private final Writer writer;

    @Transactional
    public void run() {
        new FaultTolerant(
            IntStream.range(1, this.repository.getMaxExternalId())
                .filter(i -> !this.repository.existsByExternalId(i))
                .boxed()
                .map(id -> () ->
                    this.writer.write(
                        HackernewsItemFactory.from(
                            Objects.requireNonNull(
                                this.hackernews.getItem(id).getBody()
                            )
                        )
                    )
                ),
            tr -> log.debug("Unable to fill the gap", tr.getCause())
        ).run();
    }
}
