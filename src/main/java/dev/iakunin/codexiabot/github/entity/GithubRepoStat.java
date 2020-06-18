package dev.iakunin.codexiabot.github.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.iakunin.codexiabot.common.entity.AbstractEntity;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @checkstyle MemberName (500 lines)
 */
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

    @PrePersist
    private void prePersist() {
        if (this.stat != null) {
            this.type = this.stat.getType();
        }
    }

    public enum Type {
        GITHUB_API,
        LINES_OF_CODE,
        HEALTH_CHECK
    }

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
    )
    @JsonSubTypes({
        @JsonSubTypes.Type(value = GithubApi.class, name = "GITHUB_API"),
        @JsonSubTypes.Type(value = LinesOfCode.class, name = "LINES_OF_CODE"),
        @JsonSubTypes.Type(value = HealthCheck.class, name = "HEALTH_CHECK")
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

}
