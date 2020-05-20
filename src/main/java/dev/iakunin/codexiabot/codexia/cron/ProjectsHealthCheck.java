package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectsHealthCheck implements Runnable {

    private final CodexiaClient codexiaClient;

    private final CodexiaProjectRepository repository;

    private final GithubModule githubModule;

    @Transactional
    public void run() {
        try (var projects = this.repository.findAllActive()) {
            new FaultTolerant(
                projects
                    .map(project -> this.codexiaClient.getProject(project.getExternalId()))
                    .map(HttpEntity::getBody)
                    .map(Objects::requireNonNull)
                    .filter(project -> project.getDeleted() != null)
                    .map(project -> () -> {
                        this.deleteRepoSources(project);
                        this.updateEntity(project);
                    }),
                tr -> log.error("Unable to process project", tr.getCause())
            ).run();
        }
    }

    private void deleteRepoSources(CodexiaClient.Project project) {
        this.githubModule.removeAllRepoSources(
            new GithubModule.DeleteArguments(
                GithubModule.Source.CODEXIA,
                String.valueOf(project.getId())
            )
        );
    }

    private void updateEntity(CodexiaClient.Project project) {
        final CodexiaProject foundCodexiaProject = this.repository
            .findByExternalId(project.getId())
            .orElseThrow(
                () -> new RuntimeException(
                    String.format(
                        "Unable to find CodexiaProject by externalId='%s'",
                        project.getId()
                    )
                )
            );
        this.repository.save(
            foundCodexiaProject.setDeleted(project.getDeleted())
        );
    }
}
