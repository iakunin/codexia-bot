package dev.iakunin.codexiabot.codexia.cron;

import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class UpdateProjectsUnitTest {

    private final Faker faker = new Faker();

    @Mock
    private CodexiaProjectRepository repository;

    @Mock
    private CodexiaClient codexiaClient;

    @Mock
    private UpdateProjects.Updater updater;

    @InjectMocks
    private UpdateProjects updateProjects;

    @Test
    public void noActiveProjectsInRepo() {
        Mockito.when(repository.findAllActive()).thenReturn(Stream.empty());

        updateProjects.run();

        Mockito.verify(codexiaClient, Mockito.never()).getProject(Mockito.any());
        Mockito.verify(updater, Mockito.never()).update(Mockito.any());
    }

    @Test
    public void happyPath() {
        final int externalId = faker.random().nextInt(Integer.MAX_VALUE);
        final var project = new CodexiaClient.Project().setId(externalId);
        Mockito.when(repository.findAllActive()).thenReturn(
            Stream.of(
                new CodexiaProject().setExternalId(externalId)
            )
        );
        Mockito.when(codexiaClient.getProject(externalId)).thenReturn(
            new ResponseEntity<>(project, HttpStatus.OK)
        );

        updateProjects.run();

        Mockito.verify(codexiaClient, Mockito.times(1)).getProject(externalId);
        Mockito.verify(updater, Mockito.times(1)).update(project);
    }

    @Test
    public void exceptionDuringGettingProject() {
        final int externalId = faker.random().nextInt(Integer.MAX_VALUE);
        Mockito.when(repository.findAllActive()).thenReturn(
            Stream.of(
                new CodexiaProject().setExternalId(externalId)
            )
        );
        Mockito.when(codexiaClient.getProject(externalId)).thenThrow(
            new RuntimeException("Unable to get project from Codexia")
        );

        updateProjects.run();

        Mockito.verify(codexiaClient).getProject(externalId);
        Mockito.verify(updater, Mockito.never()).update(Mockito.any());
    }

    @Test
    public void responseBodyIsNull() {
        final int externalId = faker.random().nextInt(Integer.MAX_VALUE);
        Mockito.when(repository.findAllActive()).thenReturn(
            Stream.of(
                new CodexiaProject().setExternalId(externalId)
            )
        );
        Mockito.when(codexiaClient.getProject(externalId)).thenReturn(
            new ResponseEntity<>(null, HttpStatus.OK)
        );

        updateProjects.run();

        Mockito.verify(codexiaClient).getProject(externalId);
        Mockito.verify(updater, Mockito.never()).update(Mockito.any());
    }

    @Test
    public void oneSuccessfulAfterOneExceptional() {
        final int first = faker.random().nextInt(Integer.MAX_VALUE);
        final int second = faker.random().nextInt(Integer.MAX_VALUE);
        final var project = new CodexiaClient.Project().setId(second);
        Mockito.when(repository.findAllActive()).thenReturn(
            Stream.of(
                new CodexiaProject().setExternalId(first),
                new CodexiaProject().setExternalId(second)
            )
        );
        Mockito.when(codexiaClient.getProject(first)).thenReturn(
            new ResponseEntity<>(null, HttpStatus.OK)
        );
        Mockito.when(codexiaClient.getProject(second)).thenReturn(
            new ResponseEntity<>(project, HttpStatus.OK)
        );

        updateProjects.run();

        Mockito.verify(codexiaClient, Mockito.times(1)).getProject(first);
        Mockito.verify(codexiaClient, Mockito.times(1)).getProject(second);
        Mockito.verify(updater, Mockito.times(1)).update(project);
    }
}
