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
public final class TopStories {

    private Hackernews hackernewsClient;

    private ItemListSaver itemListSaver;

//    @Scheduled(cron="40 * * * * *") // every minute at 40th second
    public void run() {
        log.info("Saving TopStories");
        this.itemListSaver.save(
            Objects.requireNonNull(
                this.hackernewsClient.getTopStories().getBody()
            )
        );
    }
}
