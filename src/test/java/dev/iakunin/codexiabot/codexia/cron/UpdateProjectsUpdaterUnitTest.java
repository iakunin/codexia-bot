package dev.iakunin.codexiabot.codexia.cron;

import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.codexia.cron.updateprojects.Updater;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.github.GithubModule;
import java.util.Optional;
import org.cactoos.list.ListOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateProjectsUpdaterUnitTest {

    private static final String NEWBIE = "newbie";

    private static final String SECOND_LEVEL = "L2";

    private final Faker faker = new Faker();

    @Mock
    private GithubModule github;

    @Mock
    private CodexiaProjectRepository repository;

    @InjectMocks
    private Updater updater;

    @Test
    public void notDeleted() {
        final int id = this.faker.random().nextInt(Integer.MAX_VALUE);
        final var project = new CodexiaClient.Project().setId(id);
        final var entity = new CodexiaProject().setExternalId(id);
        Mockito.when(this.repository.findByExternalId(id)).thenReturn(Optional.of(entity));

        this.updater.update(project);

        Mockito.verify(this.github, Mockito.never()).removeAllRepoSources(Mockito.any());
        Mockito.verify(this.repository, Mockito.times(1)).findByExternalId(id);
        Mockito.verify(this.repository, Mockito.times(1)).save(entity);
    }

    @Test
    public void deleted() {
        final int id = this.faker.random().nextInt(Integer.MAX_VALUE);
        final var deleted = "deleted";
        final var project = new CodexiaClient.Project()
            .setId(id)
            .setDeleted(deleted);
        final var entity = new CodexiaProject().setExternalId(id);
        Mockito.when(this.repository.findByExternalId(id)).thenReturn(Optional.of(entity));

        this.updater.update(project);

        Mockito.verify(this.github, Mockito.times(1)).removeAllRepoSources(
            new GithubModule.DeleteArguments(
                GithubModule.Source.CODEXIA,
                String.valueOf(id)
            )
        );
        Mockito.verify(this.repository, Mockito.times(1)).findByExternalId(id);
        Mockito.verify(this.repository, Mockito.times(1)).save(
            entity.setDeleted(deleted)
        );
    }

    @Test
    public void notFoundInRepo() {
        final int id = this.faker.random().nextInt(Integer.MAX_VALUE);
        final var project = new CodexiaClient.Project().setId(id);
        Mockito.when(this.repository.findByExternalId(id)).thenReturn(Optional.empty());

        final RuntimeException exception = Assertions.assertThrows(
            RuntimeException.class,
            () -> this.updater.update(project)
        );
        Assertions.assertEquals(
            exception.getMessage(),
            String.format(
                "Unable to find CodexiaProject by externalId='%s'",
                id
            )
        );

        Mockito.verify(this.github, Mockito.never()).removeAllRepoSources(Mockito.any());
        Mockito.verify(this.repository, Mockito.times(1)).findByExternalId(id);
        Mockito.verify(this.repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void oneBadge() {
        final int id = this.faker.random().nextInt(Integer.MAX_VALUE);
        final var project = new CodexiaClient.Project()
            .setId(id)
            .setBadges(new ListOf<>(
                new CodexiaClient.Project.Badge().setText(NEWBIE)
            ));
        final var entity = new CodexiaProject().setExternalId(id);
        Mockito.when(this.repository.findByExternalId(id)).thenReturn(Optional.of(entity));

        this.updater.update(project);

        Mockito.verify(this.github, Mockito.never()).removeAllRepoSources(Mockito.any());
        Mockito.verify(this.repository, Mockito.times(1)).findByExternalId(id);
        Mockito.verify(this.repository, Mockito.times(1)).save(
            entity.setBadges(new ListOf<>(NEWBIE))
        );
    }

    @Test
    public void multipleBadges() {
        final int id = this.faker.random().nextInt(Integer.MAX_VALUE);
        final var project = new CodexiaClient.Project()
            .setId(id)
            .setBadges(new ListOf<>(
                new CodexiaClient.Project.Badge().setText(NEWBIE),
                new CodexiaClient.Project.Badge().setText(SECOND_LEVEL)
            ));
        final var entity = new CodexiaProject().setExternalId(id);
        Mockito.when(this.repository.findByExternalId(id)).thenReturn(Optional.of(entity));

        this.updater.update(project);

        Mockito.verify(this.github, Mockito.never()).removeAllRepoSources(Mockito.any());
        Mockito.verify(this.repository, Mockito.times(1)).findByExternalId(id);
        Mockito.verify(this.repository, Mockito.times(1)).save(
            entity.setBadges(new ListOf<>(NEWBIE, SECOND_LEVEL))
        );
    }
}
