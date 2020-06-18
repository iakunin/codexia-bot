package dev.iakunin.codexiabot.hackernews.factory;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import java.time.Instant;
import java.util.Optional;

public final class HackernewsItemFactory {
    private HackernewsItemFactory() { }

    public static HackernewsItem from(final HackernewsClient.Item item) {
        return new HackernewsItem()
            .setExternalId(item.getId())
            .setType(item.getType())
            .setBy(
                Optional.ofNullable(item.getBy()).orElse("")
            )
            .setTitle(
                Optional.ofNullable(item.getTitle()).orElse("")
            )
            .setUrl(
                Optional.ofNullable(item.getUrl()).orElse("")
            )
            .setTime(
                Optional.ofNullable(item.getTime()).orElse(Instant.ofEpochSecond(0))
            )
            .setDeleted(item.isDeleted());
    }

    public static void mutateEntity(
        final HackernewsItem mutation,
        final HackernewsClient.Item item
    ) {
        mutation
            .setType(item.getType())
            .setBy(
                Optional.ofNullable(item.getBy()).orElse("")
            )
            .setTitle(
                Optional.ofNullable(item.getTitle()).orElse("")
            )
            .setUrl(
                Optional.ofNullable(item.getUrl()).orElse("")
            )
            .setTime(
                Optional.ofNullable(item.getTime()).orElse(Instant.ofEpochSecond(0))
            )
            .setDeleted(item.isDeleted());
    }
}
