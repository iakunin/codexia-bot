package dev.iakunin.codexiabot.github.entity;

import dev.iakunin.codexiabot.common.entity.AbstractEntity;
import dev.iakunin.codexiabot.github.GithubModule;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SQLDelete(sql = "UPDATE github_repo_source SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at is null")
public final class GithubRepoSource extends AbstractEntity {
    @ManyToOne
    private GithubRepo githubRepo;

    @Enumerated(EnumType.STRING)
    private GithubModule.Source source;

    private String externalId;

    private LocalDateTime deletedAt;
}
