package dev.iakunin.codexiabot.github.entity;

import dev.iakunin.codexiabot.common.entity.AbstractEntity;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.kohsuke.github.GHRepository;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class GithubRepo extends AbstractEntity {

    private String externalId;
    private String fullName;
    private String htmlUrl;
    private LocalDateTime repoCreatedAt;

    public static final class Factory {
        private Factory() {}

        @SneakyThrows
        public static GithubRepo from(GHRepository from) {
            return new GithubRepo()
                .setExternalId(String.valueOf(from.getId()))
                .setFullName(from.getFullName())
                .setHtmlUrl(from.getHtmlUrl().toString())
                .setRepoCreatedAt(
                    Factory.toLocalDateTIme(from.getCreatedAt())
                )
            ;
        }

        private static LocalDateTime toLocalDateTIme(Date pushedAt) {
            return pushedAt.toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
        }
    }
}
