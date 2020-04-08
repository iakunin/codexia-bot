package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import dev.iakunin.codexiabot.hackernews.service.Writer;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public class GapsFiller {

    private final HackernewsItemRepository repository;
    private final HackernewsClient hackernewsClient;
    private final Writer writer;

    @Scheduled(cron="${app.cron.hackernews.gaps-filler:-}")
    @Transactional // https://stackoverflow.com/a/40593697/3456163
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.repository
            .findAbsentExternalIds(1, this.repository.getMaxExternalId())
            .map(this.hackernewsClient::getItem)
            .map(HttpEntity::getBody)
            .map(Objects::requireNonNull)
            .map(HackernewsItem.Factory::from)
            .forEach(this.writer::write)
        ;

        log.info("Exiting from {}", this.getClass().getName());
    }
}
