package dev.iakunin.codexiabot.bot.entity;

import dev.iakunin.codexiabot.common.entity.AbstractEntity;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @checkstyle MemberName (500 lines)
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class ForksUpResult extends AbstractEntity implements Result {

    @ManyToOne
    private GithubRepo githubRepo;

    @ManyToOne
    private GithubRepoStat githubRepoStat;
}
