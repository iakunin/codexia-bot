package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import dev.iakunin.codexiabot.hackernews.service.Writer;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class RetryErroneous implements Runnable{

    private static final String EMPTY_TYPE = "";

    private final HackernewsItemRepository hackernewsItemRepository;

    private final HackernewsClient hackernewsClient;

    private final Writer writer;

    public void run() {
        this.hackernewsItemRepository.findAllByType(EMPTY_TYPE)
            .forEach(
                entity -> {
                    try {
                        log.debug("Trying to get item with externalId='{}'", entity.getExternalId());
                        final HackernewsClient.Item item = this.hackernewsClient.getItem(entity.getExternalId()).getBody();
                        Objects.requireNonNull(item);
                        log.debug("Successfully got item with externalId='{}'; {}", entity.getExternalId(), item);

                        HackernewsItem.Factory.mutateEntity(entity, item);

                        this.hackernewsItemRepository.save(entity);
                        this.writer.write(entity);
                    } catch (Exception e) {
                        log.warn(
                            "Exception occurred during processing hackernews item '{}'",
                            entity.getExternalId(),
                            e
                        );
                    }
                }
            );
    }
}
