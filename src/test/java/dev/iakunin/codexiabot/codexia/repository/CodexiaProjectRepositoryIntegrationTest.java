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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class CodexiaProjectRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CodexiaProjectRepository repository;

    @Autowired
    private Faker faker;

    @Test
    @Transactional
    public void findByExternalId_found() {
        final CodexiaProject original = this.createCodexiaProject();
        entityManager.persist(original);
        entityManager.flush();

        final CodexiaProject found = repository.findByExternalId(original.getExternalId())
            .orElseThrow(RuntimeException::new);

        assertEquals(original.getUuid().toString(), found.getUuid().toString());
        assertEquals(original.getExternalId(), found.getExternalId());
        assertEquals(original.getCoordinates(), found.getCoordinates());
        assertEquals(original.getAuthor(), found.getAuthor());
        assertEquals(original.getProjectCreatedAt(), found.getProjectCreatedAt());

        entityManager.remove(found);
    }

    @Test
    @Transactional
    public void findByExternalId_notFound() {
        final Optional<CodexiaProject> optional = repository.findByExternalId(faker.random().nextInt(Integer.MAX_VALUE));

        assertTrue(optional.isEmpty());
    }

    @Test
    @Transactional
    public void existsByExternalId_found() {
        final CodexiaProject project = this.createCodexiaProject();
        entityManager.persist(project);
        entityManager.flush();

        final boolean exists = repository.existsByExternalId(project.getExternalId());

        assertTrue(exists);

        entityManager.remove(project);
    }

    @Test
    @Transactional
    public void existsByExternalId_notFound() {
        final boolean exists = repository.existsByExternalId(faker.random().nextInt(Integer.MAX_VALUE));

        assertFalse(exists);
    }

    @Test
    @Transactional
    public void findAllWithoutGithubRepo_found() {
        final CodexiaProject project = this.createCodexiaProject();
        entityManager.persist(project);
        entityManager.flush();

        final var allWithoutGithubRepo = repository.findAllActiveWithoutGithubRepo();

        assertEquals(1, allWithoutGithubRepo.count());

        entityManager.remove(project);
    }

    @Test
    @Transactional
    public void findAllWithoutGithubRepo_noCodexiaProject() {
        final var allWithoutGithubRepo = repository.findAllActiveWithoutGithubRepo();

        assertEquals(0, allWithoutGithubRepo.count());
    }

    @Test
    @Transactional
    public void findAllWithoutGithubRepo_codexiaProjectWithGithubRepo() {
        final CodexiaProject project = this.createCodexiaProject();
        final GithubRepo githubRepo = new GithubRepo()
            .setExternalId(String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)))
            .setFullName(this.getGithubRepoFullName());
        final GithubRepoSource githubRepoSource = new GithubRepoSource()
            .setExternalId(String.valueOf(project.getExternalId()))
            .setSource(GithubModule.Source.CODEXIA)
            .setGithubRepo(githubRepo);
        entityManager.persist(project);
        entityManager.persist(githubRepo);
        entityManager.persist(githubRepoSource);
        entityManager.flush();

        final var allWithoutGithubRepo = repository.findAllActiveWithoutGithubRepo();

        assertEquals(0, allWithoutGithubRepo.count());

        entityManager.remove(project);
        entityManager.remove(githubRepoSource);
        entityManager.remove(githubRepo);
    }

    private CodexiaProject createCodexiaProject() {
        return new CodexiaProject()
            .setExternalId(faker.random().nextInt(Integer.MAX_VALUE))
            .setCoordinates(this.getGithubRepoFullName())
            .setAuthor(faker.name().username())
            .setProjectCreatedAt(LocalDateTime.now());
    }

    private String getGithubRepoFullName() {
        return faker.regexify("[a-z]{2,10}/[a-z]{2,10}");
    }
}
