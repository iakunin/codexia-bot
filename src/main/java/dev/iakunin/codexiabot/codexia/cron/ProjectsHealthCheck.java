package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.github.GithubModule;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class ProjectsHealthCheck {

    private final CodexiaClient codexiaClient;
    private final CodexiaProjectRepository repository;
    private final GithubModule githubModule;

    @Scheduled(cron="${app.cron.codexia.projects-health-check:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        // @TODO: rewrite after https://github.com/yegor256/codexia/issues/97 is done
        // this.repository.findAllActiveCodexiaProjects()
        //    .stream()
        //    .foreach(
                  // call codexia api for actual project data
                      // blocked by this issue https://github.com/yegor256/codexia/issues/97
                  // if it's deleted
                      // delete all CODEXIA github_repo_sources
                      // update CodexiaProject entity

        int page = 0;
        List<CodexiaClient.Project> projectList;

        do {
            projectList = this.codexiaClient.getItem(page).getBody();
            Objects.requireNonNull(projectList);

            projectList.stream()
                .filter(
                    project -> project.getDeleted() != null
                )
                .filter(
                    project -> this.repository.existsByExternalId(project.getId())
                )
                .map(
                    CodexiaProject.Factory::from
                )
                .forEach(
                    codexiaProject -> {
                        final CodexiaProject foundCodexiaProject = this.repository
                            .findByExternalId(codexiaProject.getExternalId())
                            .orElseThrow(
                                () -> new RuntimeException(
                                    String.format(
                                        "Unable to find CodexiaProject by externalId='%s'",
                                        codexiaProject.getExternalId()
                                    )
                                )
                            );
                        if (!Objects.equals(foundCodexiaProject.getDeleted(), codexiaProject.getDeleted())) {
                            log.info(
                                "Deleted is not equal - found:{}, actual:{}; externalId:{}",
                                foundCodexiaProject.getDeleted(),
                                codexiaProject.getDeleted(),
                                codexiaProject.getExternalId()
                            );
                            this.githubModule.removeAllRepoSources(
                                new GithubModule.DeleteArguments()
                                    .setSource(GithubModule.Source.CODEXIA)
                                    .setExternalId(String.valueOf(codexiaProject.getExternalId()))
                            );
                            this.repository.save(
                                foundCodexiaProject.setDeleted(codexiaProject.getDeleted())
                            );
                        }
                    }
                );

            page++;
        } while (!projectList.isEmpty());

        log.info("Exiting from {}", this.getClass().getName());
    }
}
