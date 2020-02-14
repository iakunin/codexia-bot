package com.iakunin.codexiabot.github.entity;

import com.iakunin.codexiabot.common.entity.AbstractEntity;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.hibernate.annotations.Type;
import org.kohsuke.github.GHRepository;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class GithubRepo extends AbstractEntity {
    //@TODO: main info about github-repo should be taken from here:
    //  description: https://developer.github.com/v3/repos/
    //  example: https://api.github.com/repos/github-api/github-api
    private String externalId;
    private String fullName;
    private String htmlUrl;
    private String description;
    private LocalDateTime repoCreatedAt;
    private LocalDateTime pushedAt;
    private String language;
    private String homepage;
    private Integer forks;
    private Integer watchers;
    private Integer stars;
    private Integer releases; //@TODO: extract releases from github-api
    private Integer usedBy; //@TODO: extract releases from github-api

    //@TODO: test saving of this field
    //@TODO: fill this field via https://api.codetabs.com/v1/loc?github=github-api/github-api
    //  https://codetabs.com/count-loc/count-loc-online.html
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private LinesOfCode linesOfCode;

    @Data
    public static final class LinesOfCode {
        private List<Item> itemList;
        private LocalDateTime calculatedAt;

        @Data
        public static final class Item {
            private String language;
            private Integer files;
            private Integer lines;
            private Integer blanks;
            private Integer comments;
            private Integer linesOfCode;
        }
    }

    public final static class Factory {
        @SneakyThrows
        public static GithubRepo from(GHRepository from) {
            return new GithubRepo()
                .setExternalId(String.valueOf(from.getId()))
                .setFullName(from.getFullName())
                .setHtmlUrl(from.getHtmlUrl().toString())
                .setDescription(from.getDescription())
                .setRepoCreatedAt(
                    Factory.toLocalDateTIme(from.getCreatedAt())
                )
                .setPushedAt(
                    Factory.toLocalDateTIme(from.getPushedAt())
                )
                .setLanguage(from.getLanguage())
                .setHomepage(from.getHomepage())
                .setForks(from.getForks())
                .setWatchers(from.getWatchers())
                .setStars(from.getStargazersCount())
                ;
        }

        private static LocalDateTime toLocalDateTIme(Date pushedAt) {
            return pushedAt.toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
        }
    }
}
