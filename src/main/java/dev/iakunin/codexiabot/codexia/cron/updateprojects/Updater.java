package dev.iakunin.codexiabot.codexia.cron.updateprojects;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.github.GithubModule;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class Updater {

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
