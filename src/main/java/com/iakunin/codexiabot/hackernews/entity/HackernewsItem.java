package com.iakunin.codexiabot.hackernews.entity;

import com.iakunin.codexiabot.common.entity.AbstractEntity;
import com.iakunin.codexiabot.hackernews.sdk.client.Hackernews;
import java.time.Instant;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class HackernewsItem extends AbstractEntity {
    private String externalId;
    private String type;
    private String by;
    private String title;
    private String url;
    private Instant time;

    public final static class Factory {
        public static HackernewsItem from(Hackernews.Item item) {
            return new HackernewsItem()
                .setExternalId(item.getId())
                .setType(item.getType())
                .setBy(item.getBy())
                .setTitle(item.getTitle())
                .setUrl(item.getUrl())
                .setTime(item.getTime());
        }
    }
}
