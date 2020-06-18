package dev.iakunin.codexiabot.hackernews.entity;

import dev.iakunin.codexiabot.common.entity.AbstractEntity;
import java.time.Instant;
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
}
