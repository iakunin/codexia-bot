package dev.iakunin.codexiabot.hackernews.entity;

import dev.iakunin.codexiabot.common.entity.AbstractEntity;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import java.time.Instant;
import java.util.Optional;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @checkstyle ExplicitInitialization (500 lines)
 * @checkstyle MemberName (500 lines)
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class HackernewsItem extends AbstractEntity {

    private Integer externalId;

    private String type;

    private String by;

    private String title;

    private String url;

    private Instant time;

    private boolean deleted;

    public static final class Factory {
        private Factory() { }

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
}
