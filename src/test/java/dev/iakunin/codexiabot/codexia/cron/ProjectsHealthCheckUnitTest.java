package dev.iakunin.codexiabot.codexia.cron;

import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.github.GithubModule;
import java.util.Optional;
import org.cactoos.list.ListOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class ProjectsHealthCheckUnitTest {

    @Rule
    public final ExpectedException expectedEx = ExpectedException.none();

    private final Faker faker = new Faker();

    @Mock
    private CodexiaProjectRepository repository;

    @Mock
    private CodexiaClient codexiaClient;

    @Mock
    private GithubModule githubModule;

    @InjectMocks
    private ProjectsHealthCheck projectsHealthCheck;

    @Test
    public void noActiveProjectsInRepo() {
        Mockito.when(repository.findAllActive()).thenReturn(new ListOf<>());

        projectsHealthCheck.run();

        Mockito.verify(codexiaClient, Mockito.never()).getProject(Mockito.any());
        Mockito.verify(githubModule, Mockito.never()).removeAllRepoSources(Mockito.any());
        Mockito.verify(repository, Mockito.never()).findByExternalId(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void projectIsNotDeletedInCodexia() {
        final int externalId = faker.random().nextInt(Integer.MAX_VALUE);
        Mockito.when(repository.findAllActive()).thenReturn(
            new ListOf<>(
                new CodexiaProject().setExternalId(externalId)
            )
        );
        Mockito.when(codexiaClient.getProject(externalId)).thenReturn(
            new ResponseEntity<>(
                new CodexiaClient.Project().setDeleted(null),
                HttpStatus.OK
            )
        );

        projectsHealthCheck.run();

        Mockito.verify(codexiaClient).getProject(externalId);
        Mockito.verify(githubModule, Mockito.never()).removeAllRepoSources(Mockito.any());
        Mockito.verify(repository, Mockito.never()).findByExternalId(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void projectIsDeletedInCodexia() {
        final int externalId = faker.random().nextInt(Integer.MAX_VALUE);
        final String deleted = faker.regexify("[a-z]{2,10}");
        final CodexiaProject codexiaProject = new CodexiaProject().setExternalId(externalId);
        Mockito.when(repository.findAllActive()).thenReturn(
            new ListOf<>(codexiaProject)
        );
        Mockito.when(codexiaClient.getProject(externalId)).thenReturn(
            new ResponseEntity<>(
                new CodexiaClient.Project()
                    .setId(externalId)
                    .setDeleted(deleted),
                HttpStatus.OK
            )
        );
        Mockito.when(repository.findByExternalId(externalId)).thenReturn(
            Optional.of(codexiaProject)
        );
        Mockito.when(repository.save(codexiaProject)).thenReturn(
            codexiaProject.setDeleted(deleted)
        );

        projectsHealthCheck.run();

        Mockito.verify(codexiaClient).getProject(externalId);
        Mockito.verify(githubModule).removeAllRepoSources(
            new GithubModule.DeleteArguments()
                .setSource(GithubModule.Source.CODEXIA)
                .setExternalId(String.valueOf(externalId))
        );
        Mockito.verify(repository).findByExternalId(externalId);
        Mockito.verify(repository).save(codexiaProject.setDeleted(deleted));
    }

    @Test
    public void projectIsDeletedInCodexiaButNotFoundInRepository() {
        final int externalId = faker.random().nextInt(Integer.MAX_VALUE);
        final String deleted = faker.regexify("[a-z]{2,10}");
        Mockito.when(repository.findAllActive()).thenReturn(
            new ListOf<>(
                new CodexiaProject().setExternalId(externalId)
            )
        );
        Mockito.when(codexiaClient.getProject(externalId)).thenReturn(
            new ResponseEntity<>(
                new CodexiaClient.Project()
                    .setId(externalId)
                    .setDeleted(deleted),
                HttpStatus.OK
            )
        );
        Mockito.when(repository.findByExternalId(externalId)).thenReturn(Optional.empty());
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage(
            String.format("Unable to find CodexiaProject by externalId='%s'", externalId)
        );

        projectsHealthCheck.run();

        Mockito.verify(codexiaClient).getProject(externalId);
        Mockito.verify(githubModule).removeAllRepoSources(
            new GithubModule.DeleteArguments()
                .setSource(GithubModule.Source.CODEXIA)
                .setExternalId(String.valueOf(externalId))
        );
        Mockito.verify(repository).findByExternalId(externalId);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }
}
