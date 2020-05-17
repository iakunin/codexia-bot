package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import dev.iakunin.codexiabot.hackernews.service.Writer;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public class GapsFiller implements Runnable {

    private final HackernewsItemRepository repository;

    private final HackernewsClient hackernews;

    private final Writer writer;

    @Transactional
    public void run() {
        new FaultTolerant(
            IntStream.range(1, this.repository.getMaxExternalId())
                .parallel()
                .filter(i -> !this.repository.existsByExternalId(i))
                .boxed()
                .map(this.hackernews::getItem)
                .map(HttpEntity::getBody)
                .map(Objects::requireNonNull)
                .map(HackernewsItem.Factory::from)
                .map(item -> () -> this.writer.write(item)),
            tr -> log.debug("Unable to fill the gap", tr.getCause())
        ).run();
    }
}
