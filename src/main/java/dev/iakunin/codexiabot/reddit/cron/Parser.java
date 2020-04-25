package dev.iakunin.codexiabot.reddit.cron;

import dev.iakunin.codexiabot.github.GithubModule;
import java.io.IOException;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class Parser implements Runnable {

    private final RedditClient reddit;

    private final GithubModule github;

    public void run() {
        this.github.findAllInCodexia()
            .forEach(githubRepo ->
                StreamSupport.stream(
                    this.reddit.search()
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
                            this.github.createRepo(
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
    }
}
