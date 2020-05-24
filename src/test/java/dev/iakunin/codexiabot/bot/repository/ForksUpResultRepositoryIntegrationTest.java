package dev.iakunin.codexiabot.bot.repository;

import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.bot.entity.ForksUpResult;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import java.util.Optional;
import javax.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class ForksUpResultRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ForksUpResultRepository repository;

    @Autowired
    private Faker faker;

    @Test
    @Transactional
    public void findFirstByGithubRepo_happyPath() {
        var repo = this.createGithubRepo();
        var repoStat = this.createGithubRepoStat(repo);
        var result = this.createForksUpResult(repo, repoStat);
        entityManager.persist(repo);
        entityManager.persist(repoStat);
        entityManager.persist(result);
        entityManager.flush();

        var actual = this.repository.findFirstByGithubRepoOrderByIdDesc(repo);

        assertEquals(Optional.of(result), actual);

        entityManager.remove(result);
        entityManager.remove(repoStat);
        entityManager.remove(repo);
    }

    @Test
    @Transactional
    public void findFirstByGithubRepo_withinMultipleResults() {
        var repo = this.createGithubRepo();
        var repoStat = this.createGithubRepoStat(repo);
        var firstResult = this.createForksUpResult(repo, repoStat);
        var secondResult = this.createForksUpResult(repo, repoStat);
        entityManager.persist(repo);
        entityManager.persist(repoStat);
        entityManager.persist(firstResult);
        entityManager.persist(secondResult);
        entityManager.flush();

        var actual = this.repository.findFirstByGithubRepoOrderByIdDesc(repo);

        assertEquals(Optional.of(secondResult), actual);

        entityManager.remove(secondResult);
        entityManager.remove(firstResult);
        entityManager.remove(repoStat);
        entityManager.remove(repo);
    }

    @Test
    @Transactional
    public void findFirstByGithubRepo_noData() {
        var repo = this.createGithubRepo();
        entityManager.persist(repo);

        var actual = this.repository.findFirstByGithubRepoOrderByIdDesc(repo);

        assertEquals(Optional.empty(), actual);

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

    private GithubRepoStat createGithubRepoStat(GithubRepo repo) {
        return new GithubRepoStat()
            .setGithubRepo(repo)
            .setType(GithubRepoStat.Type.GITHUB_API)
            .setStat(new GithubRepoStat.GithubApi());
    }

    private ForksUpResult createForksUpResult(GithubRepo repo, GithubRepoStat repoStat) {
        return new ForksUpResult()
            .setGithubRepo(repo)
            .setGithubRepoStat(repoStat);
    }
}
