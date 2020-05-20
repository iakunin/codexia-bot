package dev.iakunin.codexiabot.hackernews.service;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("hackernews.service.Database")
@Slf4j
@RequiredArgsConstructor
public class Database implements Writer {

    private final HackernewsItemRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void write(HackernewsItem item) {
        log.debug("Got Hackernews.Item: {}", item);

        if (!this.repository.existsByExternalId(item.getExternalId())) {
            log.debug("Saving new Hackernews.Item: {}", item);
            this.repository.save(item);
        }
    }
}
