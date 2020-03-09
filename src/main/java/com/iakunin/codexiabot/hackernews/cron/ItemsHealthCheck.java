package com.iakunin.codexiabot.hackernews.cron;

import com.iakunin.codexiabot.github.GithubModule;
import com.iakunin.codexiabot.github.entity.GithubRepoSource;
import com.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import com.iakunin.codexiabot.hackernews.repository.jpa.HackernewsItemRepository;
import com.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import java.util.Objects;
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
    private final HackernewsClient hackernewsClient;
    private final HackernewsItemRepository hackernewsItemRepository;

    @Scheduled(cron="35 * * * * *") // every minute at 35th second
    @Transactional // https://stackoverflow.com/a/40593697/3456163
    public void run() {
        log.info("Running {}", this.getClass().getName());

        try (Stream<GithubRepoSource> allRepoSources = this.githubModule.findAllRepoSources(GithubModule.Source.HACKERNEWS)) {
            allRepoSources.parallel().forEach(
                source -> {

                    log.info("Checking hackernews item GithubRepoSource {}", source);

                    try {
                        final Integer externalId = Integer.valueOf(source.getExternalId());

                        log.info("Trying to get item with externalId='{}'", externalId);
                        final HackernewsClient.Item item = this.hackernewsClient.getItem(externalId).getBody();
                        Objects.requireNonNull(item);
                        log.info("Successfully got item with externalId='{}'; {}", externalId, item);

                        if (item.isDeleted()) {
                            log.info("Found deleted item: {}", item);
                            this.githubModule.removeAllRepoSources(
                                new GithubModule.DeleteArguments()
                                    .setSource(source.getSource())
                                    .setExternalId(source.getExternalId())
                            );

                            final HackernewsItem hackernewsItem = this.hackernewsItemRepository
                                .findByExternalId(externalId)
                                .orElseThrow(
                                    () -> new RuntimeException(
                                        String.format("Unable to find HackernewsItem by externalId='%s'", externalId)
                                    )
                                );

                            HackernewsItem.Factory.mutateEntity(hackernewsItem, item);

                            log.info("Trying to save to DB; {}", hackernewsItem);
                            this.hackernewsItemRepository.save(hackernewsItem);
                            log.info("Successfully saved to DB; {}", hackernewsItem);
                        }
                    } catch (Exception e) {
                        log.warn("Exception occurred", e);
                    }
                }
            );
        }

        log.info("Exiting from {}", this.getClass().getName());
    }
}
