package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import dev.iakunin.codexiabot.hackernews.service.Writer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public final class IncrementedParser implements Runnable {

    private final HackernewsItemRepository repository;

    private final HackernewsClient hackernews;

    private final Writer writer;

    public void run() {
        int currentExternalId = this.repository.getMaxExternalId() + 1;

        for (int errorsCount = 0; errorsCount <= 10; currentExternalId++){
            try {
                log.debug("Trying to get item with externalId='{}'", currentExternalId);
                final var item = this.hackernews.getItem(currentExternalId).getBody();
                if (item == null) {
                    log.debug("Empty response body for externalId='{}'", currentExternalId);
                    errorsCount++;
                    continue;
                }

                this.writer.write(HackernewsItem.Factory.from(item));
            } catch (final Exception ex) {
                log.error(
                    "Exception occurred during getting hackernews item '{}'",
                    currentExternalId,
                    ex
                );
                errorsCount++;
            }
        }
    }
}
