package dev.iakunin.codexiabot.hackernews.service;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("hackernews.service.Database")
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class Database implements Writer {

    private final HackernewsItemRepository repository;

    @Override
    public void write(HackernewsItem item) {
        log.info("Got Hackernews.Item: {}", item);

        if (!this.repository.existsByExternalId(item.getExternalId())) {
            log.info("Saving new Hackernews.Item: {}", item);
            this.repository.save(item);
        }
    }
}
