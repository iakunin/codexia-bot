package com.iakunin.codexiabot.github.entity;

import com.iakunin.codexiabot.common.entity.AbstractEntity;
import com.iakunin.codexiabot.github.GithubModule;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class GithubRepoSource extends AbstractEntity {
    @OneToOne
    private GithubRepo githubRepo;

    @Enumerated(EnumType.STRING)
    private GithubModule.Source source;

    private String externalId;
}
