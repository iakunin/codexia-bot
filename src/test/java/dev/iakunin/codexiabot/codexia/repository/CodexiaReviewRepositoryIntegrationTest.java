package dev.iakunin.codexiabot.codexia.repository;

import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.cactoos.list.ListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class CodexiaReviewRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CodexiaReviewRepository repository;

    @Autowired
    private Faker faker;

    @Test
    @Transactional
    public void existsByCodexiaProjectAndAuthorAndReason_happyPath() {
        var project = this.createProject();
        var author = faker.name().username();
        var reason = faker.lorem().word();
        var review = new CodexiaReview()
            .setCodexiaProject(project)
            .setAuthor(author)
            .setReason(reason)
            .setText(faker.lorem().sentence());
        entityManager.persist(project);
        entityManager.persist(review);

        var actual = this.repository.existsByCodexiaProjectAndAuthorAndReason(project, author, reason);

        assertTrue(actual);

        entityManager.remove(review);
        entityManager.remove(project);
    }

    @Test
    @Transactional
    public void existsByCodexiaProjectAndAuthorAndReason_wrongProject() {
        var project = this.createProject();
        var anotherProject = this.createProject();
        var author = faker.name().username();
        var reason = faker.lorem().word();
        var review = new CodexiaReview()
            .setCodexiaProject(project)
            .setAuthor(author)
            .setReason(reason)
            .setText(faker.lorem().sentence());
        entityManager.persist(project);
        entityManager.persist(anotherProject);
        entityManager.persist(review);

        var actual = this.repository.existsByCodexiaProjectAndAuthorAndReason(anotherProject, author, reason);

        assertFalse(actual);

        entityManager.remove(review);
        entityManager.remove(project);
        entityManager.remove(anotherProject);
    }

    @Test
    @Transactional
    public void existsByCodexiaProjectAndAuthorAndReason_wrongAuthor() {
        var project = this.createProject();
        var author = faker.name().username();
        var reason = faker.lorem().word();
        var review = new CodexiaReview()
            .setCodexiaProject(project)
            .setAuthor(author)
            .setReason(reason)
            .setText(faker.lorem().sentence());
        entityManager.persist(project);
        entityManager.persist(review);

        var actual = this.repository.existsByCodexiaProjectAndAuthorAndReason(project, faker.name().username(), reason);

        assertFalse(actual);

        entityManager.remove(review);
        entityManager.remove(project);
    }

    @Test
    @Transactional
    public void existsByCodexiaProjectAndAuthorAndReason_wrongReason() {
        var project = this.createProject();
        var author = faker.name().username();
        var reason = faker.lorem().word();
        var review = new CodexiaReview()
            .setCodexiaProject(project)
            .setAuthor(author)
            .setReason(reason)
            .setText(faker.lorem().sentence());
        entityManager.persist(project);
        entityManager.persist(review);

        var actual = this.repository.existsByCodexiaProjectAndAuthorAndReason(project, author, faker.lorem().word());

        assertFalse(actual);

        entityManager.remove(review);
        entityManager.remove(project);
    }

    @Test
    @Transactional
    public void findAll_happyPath() {
        var project = this.createProject();
        var author = faker.name().username();
        var firstReview = this.createReview(project, author);
        var secondReview = this.createReview(project, author);
        entityManager.persist(project);
        entityManager.persist(secondReview);
        entityManager.persist(firstReview);
        entityManager.flush();

        var actual = this.repository.findAllByCodexiaProjectAndAuthorOrderByIdAsc(project, author);

        assertEquals(new ListOf<>(secondReview, firstReview), actual.collect(Collectors.toList()));

        entityManager.remove(firstReview);
        entityManager.remove(secondReview);
        entityManager.remove(project);
    }

    @Test
    @Transactional
    public void findAll_wrongAuthor() {
        var author = faker.name().username();
        var project = this.createProject();
        var review = this.createReview(project, author);
        entityManager.persist(project);
        entityManager.persist(review);
        entityManager.flush();

        var actual = this.repository.findAllByCodexiaProjectAndAuthorOrderByIdAsc(project, faker.name().username());

        assertEquals(new ListOf<>(), actual.collect(Collectors.toList()));

        entityManager.remove(review);
        entityManager.remove(project);
    }

    @Test
    @Transactional
    public void findAll_wrongProject() {
        var author = faker.name().username();
        var firstProject = this.createProject();
        var secondProject = this.createProject();
        var review = this.createReview(firstProject, author);
        entityManager.persist(firstProject);
        entityManager.persist(secondProject);
        entityManager.persist(review);
        entityManager.flush();

        var actual = this.repository.findAllByCodexiaProjectAndAuthorOrderByIdAsc(secondProject, author);

        assertEquals(new ListOf<>(), actual.collect(Collectors.toList()));

        entityManager.remove(review);
        entityManager.remove(firstProject);
        entityManager.remove(secondProject);
    }

    @Test
    @Transactional
    public void findAllWithoutNotifications_happyPath() {
        var author = faker.name().username();
        var project = this.createProject();
        var review = this.createReview(project, author);
        entityManager.persist(project);
        entityManager.persist(review);
        entityManager.flush();

        var actual = this.repository.findAllWithoutNotifications();

        assertEquals(new ListOf<>(review), actual.collect(Collectors.toList()));

        entityManager.remove(review);
        entityManager.remove(project);
    }

    @Test
    @Transactional
    public void findAllWithoutNotifications_notFound() {
        var author = faker.name().username();
        var project = this.createProject();
        var review = this.createReview(project, author);
        var notification = this.createNotification(review);
        entityManager.persist(project);
        entityManager.persist(review);
        entityManager.persist(notification);
        entityManager.flush();

        var actual = this.repository.findAllWithoutNotifications();

        assertEquals(new ListOf<>(), actual.collect(Collectors.toList()));

        entityManager.remove(notification);
        entityManager.remove(review);
        entityManager.remove(project);
    }

    private CodexiaProject createProject() {
        return new CodexiaProject()
            .setExternalId(faker.random().nextInt(Integer.MAX_VALUE))
            .setCoordinates(this.getGithubRepoFullName())
            .setAuthor(faker.name().username())
            .setProjectCreatedAt(LocalDateTime.now());
    }

    private String getGithubRepoFullName() {
        return faker.regexify("[a-z]{2,10}/[a-z]{2,10}");
    }

    private CodexiaReview createReview(CodexiaProject project, String author) {
        return new CodexiaReview()
            .setCodexiaProject(project)
            .setAuthor(author)
            .setReason(faker.lorem().word())
            .setText(faker.lorem().sentence());
    }

    private CodexiaReviewNotification createNotification(CodexiaReview review) {
        return new CodexiaReviewNotification()
            .setCodexiaReview(review)
            .setStatus(CodexiaReviewNotification.Status.NEW);
    }
}
