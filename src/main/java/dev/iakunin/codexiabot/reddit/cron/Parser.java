package dev.iakunin.codexiabot.reddit.cron;

import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.GithubModule.AddSourceArguments;
import dev.iakunin.codexiabot.github.GithubModule.Source;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class Parser implements Runnable {

    private final RedditClient reddit;

    private final GithubModule github;

    @Transactional
    public void run() {
        try (var repos = this.github.findAllInCodexia()) {
            new FaultTolerant(
                repos.map(githubRepo -> () ->
                    this.redditPostIds(githubRepo)
                        .forEach(redditPostId -> this.github.addRepoSource(
                            new AddSourceArguments(githubRepo, Source.REDDIT, redditPostId)
                        ))
                ),
                tr -> log.info("Unable to add REDDIT repo source", tr.getCause())
            ).run();
        }
    }

    private Stream<String> redditPostIds(GithubRepo githubRepo) {
        return StreamSupport.stream(
            this.reddit.search()
                .query(
                    String.format("url:\"%s\"", githubRepo.getHtmlUrl())
                )
                .build()
                .spliterator(),
            false
        )
        .flatMap(s -> s.getChildren().stream())
        .map(Submission::getId);
    }
}
