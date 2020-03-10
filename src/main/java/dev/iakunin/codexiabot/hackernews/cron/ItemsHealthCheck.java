package dev.iakunin.codexiabot.hackernews.cron;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import dev.iakunin.codexiabot.hackernews.HackernewsModule;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public class ItemsHealthCheck {

    private final GithubModule githubModule;
    private final HackernewsModule hackernewsModule;

    @Scheduled(cron="35 * * * * *") // every minute at 35th second
    @Transactional // https://stackoverflow.com/a/40593697/3456163
    public void run() {
        log.info("Running {}", this.getClass().getName());

        try (Stream<GithubRepoSource> allRepoSources = this.githubModule.findAllRepoSources(GithubModule.Source.HACKERNEWS)) {
            this.hackernewsModule.healthCheckItems(
                allRepoSources.parallel()
                    .map(GithubRepoSource::getExternalId)
                    .map(Integer::valueOf)
            );
        }

        log.info("Exiting from {}", this.getClass().getName());
    }
}
