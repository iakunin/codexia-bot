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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class CodexiaReviewRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EntityManager manager;

    @Autowired
    private CodexiaReviewRepository repository;

    @Autowired
    private Faker faker;

    @Test
    @Transactional
    public void existsByCodexiaProjectAndAuthorAndReasonHappyPath() {
        final var project = this.createProject();
        final var author = this.faker.name().username();
        final var reason = this.faker.lorem().word();
        final var review = new CodexiaReview()
            .setCodexiaProject(project)
            .setAuthor(author)
            .setReason(reason)
            .setText(this.faker.lorem().sentence());
        this.manager.persist(project);
        this.manager.persist(review);

        final var actual = this.repository.existsByCodexiaProjectAndAuthorAndReason(
            project,
            author,
            reason
        );

        Assertions.assertTrue(actual);

        this.manager.remove(review);
        this.manager.remove(project);
    }

    @Test
    @Transactional
    public void existsByCodexiaProjectAndAuthorAndReasonWrongProject() {
        final var project = this.createProject();
        final var another = this.createProject();
        final var author = this.faker.name().username();
        final var reason = this.faker.lorem().word();
        final var review = new CodexiaReview()
            .setCodexiaProject(project)
            .setAuthor(author)
            .setReason(reason)
            .setText(this.faker.lorem().sentence());
        this.manager.persist(project);
        this.manager.persist(another);
        this.manager.persist(review);

        final var actual = this.repository.existsByCodexiaProjectAndAuthorAndReason(
            another,
            author,
            reason
        );

        Assertions.assertFalse(actual);

        this.manager.remove(review);
        this.manager.remove(project);
        this.manager.remove(another);
    }

    @Test
    @Transactional
    public void existsByCodexiaProjectAndAuthorAndReasonWrongAuthor() {
        final var project = this.createProject();
        final var author = this.faker.name().username();
        final var reason = this.faker.lorem().word();
        final var review = new CodexiaReview()
            .setCodexiaProject(project)
            .setAuthor(author)
            .setReason(reason)
            .setText(this.faker.lorem().sentence());
        this.manager.persist(project);
        this.manager.persist(review);

        final var actual = this.repository.existsByCodexiaProjectAndAuthorAndReason(
            project,
            this.faker.name().username(),
            reason
        );

        Assertions.assertFalse(actual);

        this.manager.remove(review);
        this.manager.remove(project);
    }

    @Test
    @Transactional
    public void existsByCodexiaProjectAndAuthorAndReasonWrongReason() {
        final var project = this.createProject();
        final var author = this.faker.name().username();
        final var reason = this.faker.lorem().word();
        final var review = new CodexiaReview()
            .setCodexiaProject(project)
            .setAuthor(author)
            .setReason(reason)
            .setText(this.faker.lorem().sentence());
        this.manager.persist(project);
        this.manager.persist(review);

        final var actual = this.repository.existsByCodexiaProjectAndAuthorAndReason(
            project,
            author,
            "another" + reason
        );

        Assertions.assertFalse(actual);

        this.manager.remove(review);
        this.manager.remove(project);
    }

    @Test
    @Transactional
    public void findAllHappyPath() {
        final var project = this.createProject();
        final var author = this.faker.name().username();
        final var first = this.createReview(project, author);
        final var second = this.createReview(project, author);
        this.manager.persist(project);
        this.manager.persist(second);
        this.manager.persist(first);
        this.manager.flush();

        final var actual = this.repository.findAllByCodexiaProjectAndAuthorOrderByIdAsc(
            project,
            author
        );

        Assertions.assertEquals(
            new ListOf<>(second, first),
            actual.collect(Collectors.toList())
        );

        this.manager.remove(first);
        this.manager.remove(second);
        this.manager.remove(project);
    }

    @Test
    @Transactional
    public void findAllWrongAuthor() {
        final var author = this.faker.name().username();
        final var project = this.createProject();
        final var review = this.createReview(project, author);
        this.manager.persist(project);
        this.manager.persist(review);
        this.manager.flush();

        final var actual = this.repository.findAllByCodexiaProjectAndAuthorOrderByIdAsc(
            project,
            this.faker.name().username()
        );

        Assertions.assertEquals(new ListOf<>(), actual.collect(Collectors.toList()));

        this.manager.remove(review);
        this.manager.remove(project);
    }

    @Test
    @Transactional
    public void findAllWrongProject() {
        final var author = this.faker.name().username();
        final var first = this.createProject();
        final var second = this.createProject();
        final var review = this.createReview(first, author);
        this.manager.persist(first);
        this.manager.persist(second);
        this.manager.persist(review);
        this.manager.flush();

        final var actual = this.repository.findAllByCodexiaProjectAndAuthorOrderByIdAsc(
            second,
            author
        );

        Assertions.assertEquals(new ListOf<>(), actual.collect(Collectors.toList()));

        this.manager.remove(review);
        this.manager.remove(first);
        this.manager.remove(second);
    }

    @Test
    @Transactional
    public void findAllWithoutNotificationsHappyPath() {
        final var author = this.faker.name().username();
        final var project = this.createProject();
        final var review = this.createReview(project, author);
        this.manager.persist(project);
        this.manager.persist(review);
        this.manager.flush();

        final var actual = this.repository.findAllWithoutNotifications();

        Assertions.assertEquals(new ListOf<>(review), actual.collect(Collectors.toList()));

        this.manager.remove(review);
        this.manager.remove(project);
    }

    @Test
    @Transactional
    public void findAllWithoutNotificationsNotFound() {
        final var author = this.faker.name().username();
        final var project = this.createProject();
        final var review = this.createReview(project, author);
        final var notification = this.createNotification(review);
        this.manager.persist(project);
        this.manager.persist(review);
        this.manager.persist(notification);
        this.manager.flush();

        final var actual = this.repository.findAllWithoutNotifications();

        Assertions.assertEquals(new ListOf<>(), actual.collect(Collectors.toList()));

        this.manager.remove(notification);
        this.manager.remove(review);
        this.manager.remove(project);
    }

    private CodexiaProject createProject() {
        return new CodexiaProject()
            .setExternalId(this.faker.random().nextInt(Integer.MAX_VALUE))
            .setCoordinates(this.getGithubRepoFullName())
            .setAuthor(this.faker.name().username())
            .setProjectCreatedAt(LocalDateTime.now());
    }

    private String getGithubRepoFullName() {
        return this.faker.regexify("[a-z]{2,10}/[a-z]{2,10}");
    }

    private CodexiaReview createReview(final CodexiaProject project, final String author) {
        return new CodexiaReview()
            .setCodexiaProject(project)
            .setAuthor(author)
            .setReason(this.faker.lorem().word())
            .setText(this.faker.lorem().sentence());
    }

    private CodexiaReviewNotification createNotification(final CodexiaReview review) {
        return new CodexiaReviewNotification()
            .setCodexiaReview(review)
            .setStatus(CodexiaReviewNotification.Status.NEW);
    }
}
