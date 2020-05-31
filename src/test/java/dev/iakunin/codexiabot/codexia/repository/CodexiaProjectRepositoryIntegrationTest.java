package dev.iakunin.codexiabot.codexia.repository;

import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class CodexiaProjectRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EntityManager manager;

    @Autowired
    private CodexiaProjectRepository repository;

    @Autowired
    private Faker faker;

    @Test
    @Transactional
    public void findByExternalIdFound() {
        final CodexiaProject original = this.createCodexiaProject();
        this.manager.persist(original);
        this.manager.flush();

        final CodexiaProject found = this.repository.findByExternalId(original.getExternalId())
            .orElseThrow(RuntimeException::new);

        Assertions.assertEquals(original.getUuid().toString(), found.getUuid().toString());
        Assertions.assertEquals(original.getExternalId(), found.getExternalId());
        Assertions.assertEquals(original.getCoordinates(), found.getCoordinates());
        Assertions.assertEquals(original.getAuthor(), found.getAuthor());
        Assertions.assertEquals(original.getProjectCreatedAt(), found.getProjectCreatedAt());

        this.manager.remove(found);
    }

    @Test
    @Transactional
    public void findByExternalIdNotFound() {
        final Optional<CodexiaProject> optional = this.repository.findByExternalId(
            this.faker.random().nextInt(Integer.MAX_VALUE)
        );

        Assertions.assertTrue(optional.isEmpty());
    }

    @Test
    @Transactional
    public void existsByExternalIdFound() {
        final CodexiaProject project = this.createCodexiaProject();
        this.manager.persist(project);
        this.manager.flush();

        final boolean exists = this.repository.existsByExternalId(project.getExternalId());

        Assertions.assertTrue(exists);

        this.manager.remove(project);
    }

    @Test
    @Transactional
    public void existsByExternalIdNotFound() {
        final boolean exists = this.repository.existsByExternalId(
            this.faker.random().nextInt(Integer.MAX_VALUE)
        );

        Assertions.assertFalse(exists);
    }

    @Test
    @Transactional
    public void findAllWithoutGithubRepoFound() {
        final CodexiaProject project = this.createCodexiaProject();
        this.manager.persist(project);
        this.manager.flush();

        final var actual = this.repository.findAllActiveWithoutGithubRepo();

        Assertions.assertEquals(1, actual.count());

        this.manager.remove(project);
    }

    @Test
    @Transactional
    public void findAllWithoutGithubRepoNoCodexiaProject() {
        final var actual = this.repository.findAllActiveWithoutGithubRepo();

        Assertions.assertEquals(0, actual.count());
    }

    @Test
    @Transactional
    public void findAllWithoutGithubRepoCodexiaProjectWithGithubRepo() {
        final CodexiaProject project = this.createCodexiaProject();
        final GithubRepo repo = new GithubRepo()
            .setExternalId(String.valueOf(this.faker.random().nextInt(Integer.MAX_VALUE)))
            .setFullName(this.getGithubRepoFullName());
        final GithubRepoSource source = new GithubRepoSource()
            .setExternalId(String.valueOf(project.getExternalId()))
            .setSource(GithubModule.Source.CODEXIA)
            .setGithubRepo(repo);
        this.manager.persist(project);
        this.manager.persist(repo);
        this.manager.persist(source);
        this.manager.flush();

        final var actual = this.repository.findAllActiveWithoutGithubRepo();

        Assertions.assertEquals(0, actual.count());

        this.manager.remove(project);
        this.manager.remove(source);
        this.manager.remove(repo);
    }

    private CodexiaProject createCodexiaProject() {
        return new CodexiaProject()
            .setExternalId(this.faker.random().nextInt(Integer.MAX_VALUE))
            .setCoordinates(this.getGithubRepoFullName())
            .setAuthor(this.faker.name().username())
            .setProjectCreatedAt(LocalDateTime.now());
    }

    private String getGithubRepoFullName() {
        return this.faker.regexify("[a-z]{2,10}/[a-z]{2,10}");
    }
}
