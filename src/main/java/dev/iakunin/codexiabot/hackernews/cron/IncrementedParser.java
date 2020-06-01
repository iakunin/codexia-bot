package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import dev.iakunin.codexiabot.hackernews.service.Writer;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public final class IncrementedParser implements Runnable {

    private static final int MAX_ERRORS = 10;

    private final HackernewsItemRepository repository;

    private final HackernewsClient hackernews;

    private final Writer writer;

    public void run() {
        int current = this.repository.getMaxExternalId() + 1;

        for (int errors = 0; errors <= MAX_ERRORS; current += 1) {
            try {
                log.debug("Trying to get item with externalId='{}'", current);
                final var item = this.hackernews.getItem(current).getBody();
                if (item == null) {
                    log.debug("Empty response body for externalId='{}'", current);
                    errors += 1;
                    continue;
                }

                this.writer.write(HackernewsItem.Factory.from(item));
            } catch (final FeignException ex) {
                log.error(
                    "Exception occurred during getting hackernews item '{}'",
                    current,
                    ex
                );
                errors += 1;
            }
        }
    }
}
