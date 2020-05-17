package dev.iakunin.codexiabot.codexia.cron;

import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.github.GithubModule;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class ProjectsHealthCheckUnitTest {

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
        Mockito.when(repository.findAllActive()).thenReturn(Stream.empty());

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
            Stream.of(
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
            Stream.of(codexiaProject)
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
            new GithubModule.DeleteArguments(
                GithubModule.Source.CODEXIA,
                String.valueOf(externalId)
            )
        );
        Mockito.verify(repository).findByExternalId(externalId);
        Mockito.verify(repository).save(codexiaProject.setDeleted(deleted));
    }

    @Test
    public void projectIsDeletedInCodexiaButNotFoundInRepository() {
        final int externalId = faker.random().nextInt(Integer.MAX_VALUE);
        final String deleted = faker.regexify("[a-z]{2,10}");
        Mockito.when(repository.findAllActive()).thenReturn(
            Stream.of(
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

        final RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> projectsHealthCheck.run()
        );

        assertEquals(
            exception.getMessage(),
            String.format("Unable to find CodexiaProject by externalId='%s'", externalId)
        );
        Mockito.verify(codexiaClient).getProject(externalId);
        Mockito.verify(githubModule).removeAllRepoSources(
            new GithubModule.DeleteArguments(
                GithubModule.Source.CODEXIA,
                String.valueOf(externalId)
            )
        );
        Mockito.verify(repository).findByExternalId(externalId);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }
}
