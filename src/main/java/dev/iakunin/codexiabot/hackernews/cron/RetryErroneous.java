package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import dev.iakunin.codexiabot.hackernews.service.Writer;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class RetryErroneous {

    private static final String EMPTY_TYPE = "";

    private final HackernewsItemRepository hackernewsItemRepository;
    private final HackernewsClient hackernewsClient;
    private final Writer writer;

    @Scheduled(cron="${app.cron.hackernews.retry-erroneous:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.hackernewsItemRepository.findAllByType(EMPTY_TYPE)
            .forEach(
                entity -> {
                    try {
                        log.info("Trying to get item with externalId='{}'", entity.getExternalId());
                        final HackernewsClient.Item item = this.hackernewsClient.getItem(entity.getExternalId()).getBody();
                        Objects.requireNonNull(item);
                        log.info("Successfully got item with externalId='{}'; {}", entity.getExternalId(), item);

                        HackernewsItem.Factory.mutateEntity(entity, item);

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

        log.info("Exiting from {}", this.getClass().getName());
    }
}
