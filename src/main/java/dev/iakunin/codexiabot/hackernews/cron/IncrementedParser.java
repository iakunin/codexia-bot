package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import dev.iakunin.codexiabot.hackernews.service.Writer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class IncrementedParser implements Runnable {

    private final HackernewsItemRepository repository;

    private final HackernewsClient hackernews;

    private final Writer writer;

    public void run() {
        int currentExternalId = this.repository.getMaxExternalId() + 1;

        for (int errorsCount = 0; errorsCount <= 10; currentExternalId++){
            try {
                log.info("Trying to get item with externalId='{}'", currentExternalId);
                final HackernewsClient.Item item = this.hackernews.getItem(currentExternalId).getBody();
                if (item == null) {
                    log.info("Empty response body for externalId='{}'", currentExternalId);
                    errorsCount++;
                    continue;
                }

                this.writer.write(HackernewsItem.Factory.from(item));
            } catch (Exception e) {
                log.error("Exception occurred during getting hackernews item '{}'", currentExternalId, e);
                errorsCount++;
            }
        }
    }
}
