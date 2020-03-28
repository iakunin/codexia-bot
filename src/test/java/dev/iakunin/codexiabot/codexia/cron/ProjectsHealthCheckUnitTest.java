package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.github.GithubModule;
import org.cactoos.list.ListOf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProjectsHealthCheckUnitTest {

    @Mock
    CodexiaProjectRepository repository;

    @Mock
    CodexiaClient codexiaClient;

    @Mock
    GithubModule githubModule;

    @InjectMocks
    ProjectsHealthCheck projectsHealthCheck;

    @Test
    public void noActiveProjectsInRepo() {
        when(repository.findAllActive()).thenReturn(new ListOf<>());

        projectsHealthCheck.run();

        verify(codexiaClient, never()).getProject(null);
        verify(githubModule, never()).removeAllRepoSources(null);
        verify(repository, never()).findByExternalId(null);
        verify(repository, never()).save(null);
    }
}
