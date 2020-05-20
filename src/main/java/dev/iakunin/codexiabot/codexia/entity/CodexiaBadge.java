package dev.iakunin.codexiabot.codexia.entity;

import dev.iakunin.codexiabot.common.entity.AbstractEntity;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SQLDelete(sql = "UPDATE codexia_badge SET deleted_at = now() WHERE id = ?")
public final class CodexiaBadge extends AbstractEntity {

    @ManyToOne
    private CodexiaProject codexiaProject;

    private String badge;

    private LocalDateTime deletedAt;
}
