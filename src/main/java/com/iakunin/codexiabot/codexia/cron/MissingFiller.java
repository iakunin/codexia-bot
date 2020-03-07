package com.iakunin.codexiabot.codexia.cron;

import com.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import com.iakunin.codexiabot.github.GithubModule;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class MissingFiller {

    private final CodexiaProjectRepository repository;
    private final GithubModule githubModule;

    @Scheduled(cron="0 0 0 * * *") // every day at 00 hours at 00 minutes and 00 seconds
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.repository.findAllWithoutGithubProject()
            .forEach(
                project -> {
                    final String url = "https://github.com/" + project.getCoordinates();
                    try {
                        this.githubModule.createRepo(
                            new GithubModule.Arguments()
                                .setUrl(url)
                                .setSource(GithubModule.Source.CODEXIA)
                                .setExternalId(String.valueOf(project.getExternalId()))
                        );
                    } catch (RuntimeException | IOException e) {
                        log.info("Unable to create github repo; source url='{}'", url, e);
                    }
                }
            );

        log.info("Exiting from {}", this.getClass().getName());
    }
}
