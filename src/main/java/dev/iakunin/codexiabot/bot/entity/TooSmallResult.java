package dev.iakunin.codexiabot.bot.entity;

import dev.iakunin.codexiabot.common.entity.AbstractEntity;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class TooSmallResult extends AbstractEntity {

    @ManyToOne
    private GithubRepo githubRepo;

    @ManyToOne
    private GithubRepoStat githubRepoStat;

    @Enumerated(EnumType.STRING)
    private State state;

    public enum State {
        SET,
        RESET,
    }
}
