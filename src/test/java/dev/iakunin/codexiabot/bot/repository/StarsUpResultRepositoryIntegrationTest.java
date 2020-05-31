package dev.iakunin.codexiabot.bot.repository;

import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.bot.entity.StarsUpResult;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class StarsUpResultRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EntityManager manager;

    @Autowired
    private StarsUpResultRepository repository;

    @Autowired
    private Faker faker;

    @Test
    @Transactional
    public void findFirstByGithubRepoHappyPath() {
        final var repo = this.createGithubRepo();
        final var stat = this.createGithubRepoStat(repo);
        final var result = this.createStarsUpResult(repo, stat);
        this.manager.persist(repo);
        this.manager.persist(stat);
        this.manager.persist(result);
        this.manager.flush();

        final var actual = this.repository.findFirstByGithubRepoOrderByIdDesc(repo);

        Assertions.assertEquals(Optional.of(result), actual);

        this.manager.remove(result);
        this.manager.remove(stat);
        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void findFirstByGithubRepoWithinMultipleResults() {
        final var repo = this.createGithubRepo();
        final var stat = this.createGithubRepoStat(repo);
        final var first = this.createStarsUpResult(repo, stat);
        final var second = this.createStarsUpResult(repo, stat);
        this.manager.persist(repo);
        this.manager.persist(stat);
        this.manager.persist(first);
        this.manager.persist(second);
        this.manager.flush();

        final var actual = this.repository.findFirstByGithubRepoOrderByIdDesc(repo);

        Assertions.assertEquals(Optional.of(second), actual);

        this.manager.remove(second);
        this.manager.remove(first);
        this.manager.remove(stat);
        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void findFirstByGithubRepoNoData() {
        final var repo = this.createGithubRepo();
        this.manager.persist(repo);

        final var actual = this.repository.findFirstByGithubRepoOrderByIdDesc(repo);

        Assertions.assertEquals(Optional.empty(), actual);

        this.manager.remove(repo);
    }

    private GithubRepo createGithubRepo() {
        return new GithubRepo()
            .setExternalId(
                String.valueOf(this.faker.random().nextInt(Integer.MAX_VALUE))
            )
            .setFullName(this.getGithubRepoFullName());
    }

    private String getGithubRepoFullName() {
        return this.faker.regexify("[a-z]{2,10}/[a-z]{2,10}");
    }

    private GithubRepoStat createGithubRepoStat(final GithubRepo repo) {
        return new GithubRepoStat()
            .setGithubRepo(repo)
            .setType(GithubRepoStat.Type.GITHUB_API)
            .setStat(new GithubRepoStat.GithubApi());
    }

    private StarsUpResult createStarsUpResult(
        final GithubRepo repo,
        final GithubRepoStat stat
    ) {
        return new StarsUpResult()
            .setGithubRepo(repo)
            .setGithubRepoStat(stat);
    }
}
