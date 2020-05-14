package dev.iakunin.codexiabot.github.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.iakunin.codexiabot.common.entity.AbstractEntity;
import dev.iakunin.codexiabot.github.sdk.CodetabsClient;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.kohsuke.github.GHRepository;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class GithubRepoStat extends AbstractEntity {

    @ManyToOne
    private GithubRepo githubRepo;

    @Enumerated(EnumType.STRING)
    private Type type;

    @org.hibernate.annotations.Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private AbstractStat stat;

    public enum Type {
        GITHUB_API,
        LINES_OF_CODE,
        HEALTH_CHECK
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = GithubApi.class, name = "GITHUB_API"),
        @JsonSubTypes.Type(value = LinesOfCode.class, name = "LINES_OF_CODE"),
        @JsonSubTypes.Type(value = HealthCheck.class, name = "HEALTH_CHECK"),
    })
    public abstract static class AbstractStat {
        public abstract Type getType();
    }

    @Data
    public static final class GithubApi extends AbstractStat {

        private String description;
        private LocalDateTime pushedAt;
        private String language;
        private String homepage;
        private Integer forks;
        private Integer watchers;
        private Integer stars;

        @Override
        public Type getType() {
            return Type.GITHUB_API;
        }
    }

    @Data
    public static final class LinesOfCode extends AbstractStat {

        private List<Item> itemList;

        @Override
        public Type getType() {
            return Type.LINES_OF_CODE;
        }

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

    @Data
    public static final class HealthCheck extends AbstractStat {

        private boolean isAlive;

        @Override
        public Type getType() {
            return Type.HEALTH_CHECK;
        }
    }

    @PrePersist
    private void prePersist() {
        if (stat != null) {
            type = stat.getType();
        }
    }


    public static final class Factory {
        private Factory() {}

        public static GithubRepoStat from(GHRepository from) {
            return new GithubRepoStat()
                .setStat(
                    new GithubApi()
                        .setDescription(from.getDescription())
                        .setPushedAt(Factory.toLocalDateTime(from.getPushedAt()))
                        .setLanguage(from.getLanguage())
                        .setHomepage(from.getHomepage())
                        .setForks(from.getForksCount())
                        .setWatchers(from.getWatchersCount())
                        .setStars(from.getStargazersCount())
                )
            ;
        }

        public static GithubRepoStat from(List<CodetabsClient.Item> from) {
            return new GithubRepoStat()
                .setStat(
                    new LinesOfCode()
                        .setItemList(
                            from.stream().map(item -> new LinesOfCode.Item()
                                .setLanguage(item.getLanguage())
                                .setFiles(item.getFiles())
                                .setLines(item.getLines())
                                .setBlanks(item.getBlanks())
                                .setComments(item.getComments())
                                .setLinesOfCode(item.getLinesOfCode())
                            ).collect(Collectors.toList())
                        )
                )
            ;
        }

        private static LocalDateTime toLocalDateTime(Date pushedAt) {
            return pushedAt.toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
        }
    }
}
