package dev.iakunin.codexiabot.hackernews;

import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import java.util.stream.Stream;

public interface HackernewsModule {
    void healthCheckItems(Stream<Integer> ids);

    HackernewsClient.Item getItem(Integer id);
}
