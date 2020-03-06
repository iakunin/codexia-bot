package com.iakunin.codexiabot.codexia.entity;

import com.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import com.iakunin.codexiabot.common.entity.AbstractEntity;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class CodexiaProject extends AbstractEntity {

    private Integer externalId;
    private String coordinates;
    private String author;
    private Integer authorId;
    private String deleted;
    private LocalDateTime projectCreatedAt;

    public final static class Factory {
        public static CodexiaProject from(CodexiaClient.Project from) {
            return new CodexiaProject()
                .setExternalId(from.getId())
                .setCoordinates(from.getCoordinates())
                .setAuthor(from.getAuthor())
                .setAuthorId(from.getAuthorId())
                .setDeleted(from.getDeleted())
                .setProjectCreatedAt(
                    Factory.toLocalDateTIme(from.getCreated())
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
