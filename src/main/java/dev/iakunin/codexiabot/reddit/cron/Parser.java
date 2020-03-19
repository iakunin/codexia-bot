package dev.iakunin.codexiabot.reddit.cron;

import dev.iakunin.codexiabot.github.GithubModule;
import java.io.IOException;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
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

    @Scheduled(cron="${app.cron.reddit.parser:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.githubModule.findAllInCodexia()
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
}
