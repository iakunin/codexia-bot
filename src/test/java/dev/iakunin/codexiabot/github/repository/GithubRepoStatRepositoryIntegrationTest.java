package dev.iakunin.codexiabot.github.repository;

import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.Type;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.cactoos.list.ListOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class GithubRepoStatRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EntityManager manager;

    @Autowired
    private GithubRepoStatRepository repository;

    @Autowired
    private Faker faker;

    @Test
    @Transactional
    public void findAllHappyPath() {
        final var repo = this.createGithubRepo();
        final var first = this.createGithubRepoStat(repo, Type.GITHUB_API);
        final var second = this.createGithubRepoStat(repo, Type.GITHUB_API);
        this.manager.persist(repo);
        this.manager.persist(first);
        this.manager.persist(second);
        this.manager.flush();

        final var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(repo, Type.GITHUB_API, 0L);

        Assertions.assertEquals(
            new ListOf<>(first, second),
            actual
        );

        this.manager.remove(second);
        this.manager.remove(first);
        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void findAllWrongRepo() {
        final var repo = this.createGithubRepo();
        final var another = this.createGithubRepo();
        final var stat = this.createGithubRepoStat(repo, Type.GITHUB_API);
        this.manager.persist(repo);
        this.manager.persist(another);
        this.manager.persist(stat);
        this.manager.flush();

        final var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(
                another,
                Type.GITHUB_API,
                0L
            );

        Assertions.assertEquals(
            new ListOf<>(),
            actual
        );

        this.manager.remove(stat);
        this.manager.remove(another);
        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void findAllWrongType() {
        final var repo = this.createGithubRepo();
        final var stat = this.createGithubRepoStat(repo, Type.GITHUB_API);
        this.manager.persist(repo);
        this.manager.persist(stat);
        this.manager.flush();

        final var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(
                repo,
                Type.LINES_OF_CODE,
                0L
            );

        Assertions.assertEquals(
            new ListOf<>(),
            actual
        );

        this.manager.remove(stat);
        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void findAllTwoById() {
        final var repo = this.createGithubRepo();
        final var type = Type.GITHUB_API;
        final var first = this.createGithubRepoStat(repo, type);
        final var second = this.createGithubRepoStat(repo, type);
        this.manager.persist(repo);
        this.manager.persist(first);
        this.manager.persist(second);
        this.manager.flush();

        final var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(repo, type, first.getId());

        Assertions.assertEquals(
            new ListOf<>(first, second),
            actual
        );

        this.manager.remove(second);
        this.manager.remove(first);
        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void findAllOneById() {
        final var repo = this.createGithubRepo();
        final var type = Type.GITHUB_API;
        final var first = this.createGithubRepoStat(repo, type);
        final var second = this.createGithubRepoStat(repo, type);
        this.manager.persist(repo);
        this.manager.persist(first);
        this.manager.persist(second);
        this.manager.flush();

        final var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(
                repo,
                type,
                second.getId()
            );

        Assertions.assertEquals(
            new ListOf<>(second),
            actual
        );

        this.manager.remove(second);
        this.manager.remove(first);
        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void findAllZeroById() {
        final var repo = this.createGithubRepo();
        final var type = Type.GITHUB_API;
        final var first = this.createGithubRepoStat(repo, type);
        final var second = this.createGithubRepoStat(repo, type);
        this.manager.persist(repo);
        this.manager.persist(first);
        this.manager.persist(second);
        this.manager.flush();

        final var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(
                repo,
                type,
                second.getId() + 1
            );

        Assertions.assertEquals(
            new ListOf<>(),
            actual
        );

        this.manager.remove(second);
        this.manager.remove(first);
        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void findFirstHappyPath() {
        final var repo = this.createGithubRepo();
        final var type = Type.GITHUB_API;
        final var first = this.createGithubRepoStat(repo, type);
        final var second = this.createGithubRepoStat(repo, type);
        this.manager.persist(repo);
        this.manager.persist(first);
        this.manager.persist(second);
        this.manager.flush();

        final var actual = this.repository
            .findFirstByGithubRepoAndTypeOrderByIdDesc(repo, type);

        Assertions.assertEquals(
            Optional.of(second),
            actual
        );

        this.manager.remove(second);
        this.manager.remove(first);
        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void findFirstEmpty() {
        final var repo = this.createGithubRepo();
        final var type = Type.GITHUB_API;
        this.manager.persist(repo);
        this.manager.flush();

        final var actual = this.repository
            .findFirstByGithubRepoAndTypeOrderByIdDesc(repo, type);

        Assertions.assertEquals(
            Optional.empty(),
            actual
        );

        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void findFirstWrongType() {
        final var repo = this.createGithubRepo();
        final var type = Type.GITHUB_API;
        final var stat = this.createGithubRepoStat(repo, type);
        this.manager.persist(repo);
        this.manager.persist(stat);
        this.manager.flush();

        final var actual = this.repository
            .findFirstByGithubRepoAndTypeOrderByIdDesc(repo, Type.LINES_OF_CODE);

        Assertions.assertEquals(
            Optional.empty(),
            actual
        );

        this.manager.remove(stat);
        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void persistStatWithEmptyStat() {
        final var repo = this.createGithubRepo();
        final var stat = new GithubRepoStat().setGithubRepo(repo).setType(Type.GITHUB_API);
        this.manager.persist(repo);
        this.manager.persist(stat);
        this.manager.flush();

        this.manager.remove(stat);
        this.manager.remove(repo);
    }

    @Test
    @Transactional
    public void persistHealthCheckStat() {
        final var repo = this.createGithubRepo();
        final var stat = new GithubRepoStat()
            .setGithubRepo(repo)
            .setType(Type.HEALTH_CHECK)
            .setStat(new GithubRepoStat.HealthCheck());
        this.manager.persist(repo);
        this.manager.persist(stat);
        this.manager.flush();

        this.manager.remove(stat);
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

    private GithubRepoStat createGithubRepoStat(final GithubRepo repo, final Type type) {
        return new GithubRepoStat()
            .setGithubRepo(repo)
            .setType(type)
            .setStat(new GithubRepoStat.GithubApi());
    }
}
