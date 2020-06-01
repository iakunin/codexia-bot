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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
@RequiredArgsConstructor
public final class UpdateProjects implements Runnable {

    private static final int PAGE_SIZE = 100;

    private final CodexiaProjectRepository repository;

    private final CodexiaClient codexia;

    private final Updater updater;

    public void run() {
        Page<CodexiaProject> page;
        int num = 0;
        do {
            page = this.repository.findAllActive(PageRequest.of(num, PAGE_SIZE));
            new FaultTolerant(
                page.stream()
                    .map(project -> () ->
                        this.updater.update(
                            Objects.requireNonNull(
                                this.codexia
                                    .getProject(project.getExternalId())
                                    .getBody()
                            )
                        )
                    ),
                tr -> log.error("Unable to update project", tr.getCause())
            ).run();
            num += 1;
        } while (page.hasNext());
    }

    @Service
    @RequiredArgsConstructor
    public static final class Updater {

        private final GithubModule github;

        private final CodexiaProjectRepository repository;

        public void update(final CodexiaClient.Project project) {
            if (project.getDeleted() != null) {
                this.deleteRepoSources(project);
            }
            this.updateEntity(project);
        }

        private void deleteRepoSources(final CodexiaClient.Project project) {
            this.github.removeAllRepoSources(
                new GithubModule.DeleteArguments(
                    GithubModule.Source.CODEXIA,
                    String.valueOf(project.getId())
                )
            );
        }

        private void updateEntity(final CodexiaClient.Project project) {
            final CodexiaProject found = this.repository
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
                found
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
