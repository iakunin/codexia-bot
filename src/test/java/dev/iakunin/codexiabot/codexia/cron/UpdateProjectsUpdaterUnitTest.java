package dev.iakunin.codexiabot.codexia.cron;

import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.github.GithubModule;
import java.util.Optional;
import org.cactoos.list.ListOf;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpdateProjectsUpdaterUnitTest {

    private final Faker faker = new Faker();

    @Mock
    private GithubModule githubModule;

    @Mock
    private CodexiaProjectRepository repository;

    @InjectMocks
    private UpdateProjects.Updater updater;

    @Test
    public void notDeleted() {
        final int id = faker.random().nextInt(Integer.MAX_VALUE);
        final var project = new CodexiaClient.Project().setId(id);
        final var entity = new CodexiaProject().setExternalId(id);
        Mockito.when(repository.findByExternalId(id)).thenReturn(Optional.of(entity));

        updater.update(project);

        Mockito.verify(githubModule, Mockito.never()).removeAllRepoSources(Mockito.any());
        Mockito.verify(repository, Mockito.times(1)).findByExternalId(id);
        Mockito.verify(repository, Mockito.times(1)).save(entity);
    }

    @Test
    public void deleted() {
        final int id = faker.random().nextInt(Integer.MAX_VALUE);
        final var project = new CodexiaClient.Project()
            .setId(id)
            .setDeleted("deleted");
        final var entity = new CodexiaProject().setExternalId(id);
        Mockito.when(repository.findByExternalId(id)).thenReturn(Optional.of(entity));

        updater.update(project);

        Mockito.verify(githubModule, Mockito.times(1)).removeAllRepoSources(
            new GithubModule.DeleteArguments(
                GithubModule.Source.CODEXIA,
                String.valueOf(id)
            )
        );
        Mockito.verify(repository, Mockito.times(1)).findByExternalId(id);
        Mockito.verify(repository, Mockito.times(1)).save(
            entity.setDeleted("deleted")
        );
    }

    @Test
    public void notFoundInRepo() {
        final int id = faker.random().nextInt(Integer.MAX_VALUE);
        final var project = new CodexiaClient.Project().setId(id);
        Mockito.when(repository.findByExternalId(id)).thenReturn(Optional.empty());

        final RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> updater.update(project)
        );
        assertEquals(
            exception.getMessage(),
            String.format(
                "Unable to find CodexiaProject by externalId='%s'",
                id
            )
        );

        Mockito.verify(githubModule, Mockito.never()).removeAllRepoSources(Mockito.any());
        Mockito.verify(repository, Mockito.times(1)).findByExternalId(id);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void oneBadge() {
        final int id = faker.random().nextInt(Integer.MAX_VALUE);
        final var project = new CodexiaClient.Project()
            .setId(id)
            .setBadges(new ListOf<>(
                new CodexiaClient.Project.Badge()
                    .setId(123)
                    .setText("newbie")
            ));
        final var entity = new CodexiaProject().setExternalId(id);
        Mockito.when(repository.findByExternalId(id)).thenReturn(Optional.of(entity));

        updater.update(project);

        Mockito.verify(githubModule, Mockito.never()).removeAllRepoSources(Mockito.any());
        Mockito.verify(repository, Mockito.times(1)).findByExternalId(id);
        Mockito.verify(repository, Mockito.times(1)).save(
            entity.setBadges(new ListOf<>("newbie"))
        );
    }

    @Test
    public void multipleBadges() {
        final int id = faker.random().nextInt(Integer.MAX_VALUE);
        final var project = new CodexiaClient.Project()
            .setId(id)
            .setBadges(new ListOf<>(
                new CodexiaClient.Project.Badge()
                    .setId(123)
                    .setText("newbie"),
                new CodexiaClient.Project.Badge()
                    .setId(321)
                    .setText("L2")
            ));
        final var entity = new CodexiaProject().setExternalId(id);
        Mockito.when(repository.findByExternalId(id)).thenReturn(Optional.of(entity));

        updater.update(project);

        Mockito.verify(githubModule, Mockito.never()).removeAllRepoSources(Mockito.any());
        Mockito.verify(repository, Mockito.times(1)).findByExternalId(id);
        Mockito.verify(repository, Mockito.times(1)).save(
            entity.setBadges(new ListOf<>("newbie", "L2"))
        );
    }
}
