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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        when(repository.findAllActive()).thenReturn(new ListOf<>());

        projectsHealthCheck.run();

        verify(codexiaClient, never()).getProject(null);
        verify(githubModule, never()).removeAllRepoSources(null);
        verify(repository, never()).findByExternalId(null);
        verify(repository, never()).save(null);
    }

    @Test
    public void projectIsNotDeletedInCodexia() {
        final int externalId = faker.random().nextInt(Integer.MAX_VALUE);
        when(repository.findAllActive()).thenReturn(
            new ListOf<>(
                new CodexiaProject().setExternalId(externalId)
            )
        );
        when(codexiaClient.getProject(externalId)).thenReturn(
            new ResponseEntity<>(
                new CodexiaClient.Project().setDeleted(null),
                HttpStatus.OK
            )
        );

        projectsHealthCheck.run();

        verify(codexiaClient).getProject(externalId);
        verify(githubModule, never()).removeAllRepoSources(null);
        verify(repository, never()).findByExternalId(null);
        verify(repository, never()).save(null);
    }

    @Test
    public void projectIsDeletedInCodexia() {
        final int externalId = faker.random().nextInt(Integer.MAX_VALUE);
        final String deleted = faker.regexify("[a-z]{2,10}");
        when(repository.findAllActive()).thenReturn(
            new ListOf<>(
                new CodexiaProject().setExternalId(externalId)
            )
        );
        when(codexiaClient.getProject(externalId)).thenReturn(
            new ResponseEntity<>(
                new CodexiaClient.Project()
                    .setId(externalId)
                    .setDeleted(deleted),
                HttpStatus.OK
            )
        );
        when(repository.findByExternalId(externalId)).thenReturn(
            Optional.of(
                new CodexiaProject().setExternalId(externalId)
            )
        );

        projectsHealthCheck.run();

        verify(codexiaClient).getProject(externalId);
        verify(githubModule).removeAllRepoSources(
            new GithubModule.DeleteArguments()
                .setSource(GithubModule.Source.CODEXIA)
                .setExternalId(String.valueOf(externalId))
        );
        verify(repository).findByExternalId(externalId);
        verify(repository).save(
            new CodexiaProject()
                .setExternalId(externalId)
                .setDeleted(deleted)
        );
    }

    @Test
    public void projectIsDeletedInCodexiaButNotFoundInRepository() {
        final int externalId = faker.random().nextInt(Integer.MAX_VALUE);
        final String deleted = faker.regexify("[a-z]{2,10}");
        when(repository.findAllActive()).thenReturn(
            new ListOf<>(
                new CodexiaProject().setExternalId(externalId)
            )
        );
        when(codexiaClient.getProject(externalId)).thenReturn(
            new ResponseEntity<>(
                new CodexiaClient.Project()
                    .setId(externalId)
                    .setDeleted(deleted),
                HttpStatus.OK
            )
        );
        when(repository.findByExternalId(externalId)).thenReturn(Optional.empty());
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage(
            String.format(
                "Unable to find CodexiaProject by externalId='%s'",
                externalId
            )
        );

        projectsHealthCheck.run();

        verify(codexiaClient).getProject(externalId);
        verify(githubModule).removeAllRepoSources(
            new GithubModule.DeleteArguments()
                .setSource(GithubModule.Source.CODEXIA)
                .setExternalId(String.valueOf(externalId))
        );
        verify(repository).findByExternalId(externalId);
        verify(repository, never()).save(null);
    }
}
