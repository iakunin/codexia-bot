package dev.iakunin.codexiabot.github.repository;

import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.Type;
import java.util.LinkedList;
import javax.persistence.EntityManager;
import org.cactoos.collection.CollectionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class GithubRepoStatRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GithubRepoStatRepository repository;

    @Autowired
    private Faker faker;

    @Test
    @Transactional
    public void findAll_happyPath() {
        var repo = this.createGithubRepo();
        var firstStat = this.createGithubRepoStat(repo, Type.GITHUB_API);
        var secondStat = this.createGithubRepoStat(repo, Type.GITHUB_API);
        entityManager.persist(repo);
        entityManager.persist(firstStat);
        entityManager.persist(secondStat);
        entityManager.flush();

        var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(repo, Type.GITHUB_API, 0L);

        assertEquals(
            new LinkedList<>(new CollectionOf<>(firstStat, secondStat)),
            actual
        );

        entityManager.remove(secondStat);
        entityManager.remove(firstStat);
        entityManager.remove(repo);
    }

    @Test
    @Transactional
    public void findAll_wrongRepo() {
        var repo = this.createGithubRepo();
        var anotherRepo = this.createGithubRepo();
        var stat = this.createGithubRepoStat(repo, Type.GITHUB_API);
        entityManager.persist(repo);
        entityManager.persist(anotherRepo);
        entityManager.persist(stat);
        entityManager.flush();

        var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(anotherRepo, Type.GITHUB_API, 0L);

        assertEquals(
            new LinkedList<>(new CollectionOf<>()),
            actual
        );

        entityManager.remove(stat);
        entityManager.remove(anotherRepo);
        entityManager.remove(repo);
    }

    @Test
    @Transactional
    public void findAll_wrongType() {
        var repo = this.createGithubRepo();
        var stat = this.createGithubRepoStat(repo, Type.GITHUB_API);
        entityManager.persist(repo);
        entityManager.persist(stat);
        entityManager.flush();

        var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(repo, Type.LINES_OF_CODE, 0L);

        assertEquals(
            new LinkedList<>(new CollectionOf<>()),
            actual
        );

        entityManager.remove(stat);
        entityManager.remove(repo);
    }

    @Test
    @Transactional
    public void findAll_twoById() {
        var repo = this.createGithubRepo();
        var type = Type.GITHUB_API;
        var firstStat = this.createGithubRepoStat(repo, type);
        var secondStat = this.createGithubRepoStat(repo, type);
        entityManager.persist(repo);
        entityManager.persist(firstStat);
        entityManager.persist(secondStat);
        entityManager.flush();

        var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(repo, type, firstStat.getId());

        assertEquals(
            new LinkedList<>(new CollectionOf<>(firstStat, secondStat)),
            actual
        );

        entityManager.remove(secondStat);
        entityManager.remove(firstStat);
        entityManager.remove(repo);
    }

    @Test
    @Transactional
    public void findAll_oneById() {
        var repo = this.createGithubRepo();
        var type = Type.GITHUB_API;
        var firstStat = this.createGithubRepoStat(repo, type);
        var secondStat = this.createGithubRepoStat(repo, type);
        entityManager.persist(repo);
        entityManager.persist(firstStat);
        entityManager.persist(secondStat);
        entityManager.flush();

        var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(repo, type, secondStat.getId());

        assertEquals(
            new LinkedList<>(new CollectionOf<>(secondStat)),
            actual
        );

        entityManager.remove(secondStat);
        entityManager.remove(firstStat);
        entityManager.remove(repo);
    }

    @Test
    @Transactional
    public void findAll_zeroById() {
        var repo = this.createGithubRepo();
        var type = Type.GITHUB_API;
        var firstStat = this.createGithubRepoStat(repo, type);
        var secondStat = this.createGithubRepoStat(repo, type);
        entityManager.persist(repo);
        entityManager.persist(firstStat);
        entityManager.persist(secondStat);
        entityManager.flush();

        var actual = this.repository
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(repo, type, secondStat.getId() + 1);

        assertEquals(
            new LinkedList<>(new CollectionOf<>()),
            actual
        );

        entityManager.remove(secondStat);
        entityManager.remove(firstStat);
        entityManager.remove(repo);
    }

    private GithubRepo createGithubRepo() {
        return new GithubRepo()
            .setExternalId(
                String.valueOf(faker.random().nextInt(Integer.MAX_VALUE))
            )
            .setFullName(this.getGithubRepoFullName());
    }

    private String getGithubRepoFullName() {
        return faker.regexify("[a-z]{2,10}/[a-z]{2,10}");
    }

    private GithubRepoStat createGithubRepoStat(GithubRepo repo, Type type) {
        return new GithubRepoStat()
            .setGithubRepo(repo)
            .setType(type)
            .setStat(new GithubRepoStat.GithubApi());
    }

}
