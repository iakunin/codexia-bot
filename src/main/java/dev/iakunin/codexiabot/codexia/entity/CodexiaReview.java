package dev.iakunin.codexiabot.codexia.entity;

import dev.iakunin.codexiabot.common.entity.AbstractEntity;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @checkstyle MemberName (500 lines)
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class CodexiaReview extends AbstractEntity {

    @ManyToOne
    private CodexiaProject codexiaProject;

    private String author;
    private String reason;
    private String text;
}
