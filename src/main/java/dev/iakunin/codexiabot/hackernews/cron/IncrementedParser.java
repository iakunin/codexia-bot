package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import dev.iakunin.codexiabot.hackernews.service.Writer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class IncrementedParser {

    private final HackernewsItemRepository hackernewsItemRepository;
    private final HackernewsClient hackernewsClient;
    private final Writer writer;

    @Scheduled(cron="${app.cron.hackernews.incremented-parser:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        int currentExternalId = this.hackernewsItemRepository.getMaxExternalId() + 1;

        for (int errorsCount = 0; errorsCount <= 10; currentExternalId++){
            try {
                log.info("Trying to get item with externalId='{}'", currentExternalId);
                final HackernewsClient.Item item = this.hackernewsClient.getItem(currentExternalId).getBody();
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

        log.info("Exiting from {}", this.getClass().getName());
    }
}
