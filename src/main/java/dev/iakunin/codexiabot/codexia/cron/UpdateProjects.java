package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateProjects implements Runnable {

    private final CodexiaProjectRepository repository;

    private final CodexiaClient codexiaClient;

    private final Updater updater;

    @Transactional
    public void run() {
        try (var projects = this.repository.findAllActive()) {
            new FaultTolerant(
                projects
                    .map(project -> () ->
                        this.updater.update(
                            Objects.requireNonNull(
                                this.codexiaClient
                                    .getProject(project.getExternalId())
                                    .getBody()
                            )
                        )
                    ),
                tr -> log.error("Unable to update project", tr.getCause())
            ).run();
        }
    }

    @Service
    @RequiredArgsConstructor
    public static class Updater {

        private final GithubModule githubModule;

        private final CodexiaProjectRepository repository;

        public void update(CodexiaClient.Project project) {
            if (project.getDeleted() != null) {
                this.deleteRepoSources(project);
            }
            this.updateEntity(project);
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
                foundCodexiaProject
                    .setDeleted(project.getDeleted())
                    .setBadges(
                        project.getBadges().stream()
                            .map(CodexiaClient.Project.Badge::getText)
                            .collect(Collectors.toList())
                    )
            );
        }
    }
}
