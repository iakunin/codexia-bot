package com.iakunin.codexiabot.hackernews.service;

import com.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import com.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import com.iakunin.codexiabot.hackernews.sdk.client.Hackernews;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class ItemListSaver {

    private HackernewsItemRepository hackernewsItemRepository;
    private Hackernews hackernewsClient;

    public void save(List<String> itemIdList) {
        this.filterExisting(itemIdList)
        .forEach(
            id -> {
                final Hackernews.Item item = Objects.requireNonNull(this.hackernewsClient.getItem(id).getBody());
                log.info("Saving item: {}", item);

                this.hackernewsItemRepository.save(
                    HackernewsItem.Factory.from(item)
                );
            }
        );
    }

    private List<String> filterExisting(List<String> idList) {
        if (idList.isEmpty()) {
            return Collections.emptyList();
        }

        idList.removeAll(this.hackernewsItemRepository.findExternalIdList(idList));

        return idList;
    }
}
