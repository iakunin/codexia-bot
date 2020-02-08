package com.iakunin.codexiabot.hackernews.cron;

import com.iakunin.codexiabot.hackernews.sdk.client.Hackernews;
import com.iakunin.codexiabot.hackernews.service.ItemListSaver;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class BestStories {

    private Hackernews hackernewsClient;

    private ItemListSaver itemListSaver;

//    @Scheduled(cron="0 * * * * *") // every minute at 0th second
    public void calculateTourSchedules() {
        log.info("Saving BestStories");
        this.itemListSaver.save(
            Objects.requireNonNull(
                this.hackernewsClient.getBestStories().getBody()
            )
        );
    }

}
