package dev.iakunin.codexiabot.codexia.entity;

import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.common.entity.AbstractEntity;
import dev.iakunin.codexiabot.common.entity.converter.StringListConverter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Convert;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cactoos.list.ListOf;

/**
 * @checkstyle MemberName (500 lines)
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class CodexiaProject extends AbstractEntity {

    private Integer externalId;
    private String coordinates;
    private String author;
    private String deleted;
    private LocalDateTime projectCreatedAt;

    @Convert(converter = StringListConverter.class)
    private List<String> badges = new ListOf<>();

    public Integer level() {
        final var levels = this.badges.stream()
            .map(String::toLowerCase)
            .filter(badge -> badge.matches("^l\\d+$"))
            .collect(Collectors.toList());

        if (levels.size() > 1) {
            throw new IllegalStateException(
                String.format(
                    "Project cannot have more than one level; externalId='%d'; levels='%s'",
                    this.externalId,
                    String.join(",", levels)
                )
            );
        } else if (levels.isEmpty()) {
            return 0;
        }

        return Integer.valueOf(levels.get(0).replace("l", ""));
    }

    public static final class Factory {
        private Factory() {}

        public static CodexiaProject from(CodexiaClient.Project from) {
            return new CodexiaProject()
                .setExternalId(from.getId())
                .setCoordinates(from.getCoordinates())
                .setAuthor(from.getSubmitter().getLogin())
                .setDeleted(from.getDeleted())
                .setProjectCreatedAt(
                    Factory.toLocalDateTIme(from.getCreated())
                )
                .setBadges(
                    from.getBadges().stream()
                        .map(CodexiaClient.Project.Badge::getText)
                        .collect(Collectors.toList())
                )
            ;
        }

        private static LocalDateTime toLocalDateTIme(Date date) {
            return date.toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
        }
    }
}
