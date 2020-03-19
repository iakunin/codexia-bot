package dev.iakunin.codexiabot.reddit.cron;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.io.IOException;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class Parser {

    private final RedditClient redditClient;

    private final GithubModule githubModule;

    private final CodexiaModule codexiaModule;

    @Scheduled(cron="${app.cron.reddit.parser:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.githubModule.findAllInCodexia()
            .stream()
            .map(
                githubRepo -> {
                    final CodexiaProject codexiaProject = this.githubModule.findAllRepoSources(githubRepo)
                        .stream()
                        .filter(source -> source.getSource() == GithubModule.Source.CODEXIA)
                        .findFirst()
                        .flatMap(
                            source -> this.codexiaModule.findByExternalId(Integer.valueOf(source.getExternalId()))
                        )
                        .orElseThrow(
                            () -> new RuntimeException(
                                String.format(
                                    "Unable to find source with type'%s' for githubRepoId='%s' " +
                                        "or codexiaProject by externalId",
                                    GithubModule.Source.CODEXIA.name(),
                                    githubRepo.getId()
                                )
                            )
                        );

                    return new TmpDto(githubRepo, codexiaProject);
                }
            )
            .filter(dto -> dto.getCodexiaProject().getDeleted() == null) //@TODO: this should be in SQL-level
            .map(TmpDto::getGithubRepo)
            .forEach(githubRepo ->
                StreamSupport.stream(
                    this.redditClient.search()
                        .query(
                            String.format("url:\"%s\"", githubRepo.getHtmlUrl())
                        )
                        .build()
                        .spliterator(),
                    false
                )
                .flatMap(s -> s.getChildren().stream())
                .map(Submission::getId)
                .forEach(
                    id -> {
                        try {
                            this.githubModule.createRepo(
                                new GithubModule.CreateArguments()
                                    .setUrl(githubRepo.getHtmlUrl())
                                    .setSource(GithubModule.Source.REDDIT)
                                    .setExternalId(id)
                            );
                        } catch (IOException e) {
                            log.error("Unable to create github repo", e);
                        }
                    }
                )
            );

        log.info("Exiting from {}", this.getClass().getName());
    }


    @Value
    private static final class TmpDto {
        private GithubRepo githubRepo;
        private CodexiaProject codexiaProject;
    }
}
