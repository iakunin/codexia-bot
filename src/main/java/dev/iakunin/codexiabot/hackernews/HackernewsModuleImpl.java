package dev.iakunin.codexiabot.hackernews;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class HackernewsModuleImpl implements HackernewsModule {

    private final GithubModule githubModule;
    private final HackernewsClient hackernewsClient;
    private final HackernewsItemRepository hackernewsItemRepository;

    @Override
    public void healthCheckItems(Stream<Integer> externalIds) {
        externalIds.forEach(
            externalId -> {

                log.info("Got hackernewsExternalId {}", externalId);

                try {
                    log.info("Trying to get item with externalId='{}'", externalId);
                    final HackernewsClient.Item item = this.hackernewsClient.getItem(externalId).getBody();
                    Objects.requireNonNull(item);
                    log.info("Successfully got item with externalId='{}'; {}", externalId, item);

                    if (item.isDeleted()) {
                        log.info("Found deleted item: {}", item);
                        this.githubModule.removeAllRepoSources(
                            new GithubModule.DeleteArguments()
                                .setSource(GithubModule.Source.HACKERNEWS)
                                .setExternalId(String.valueOf(externalId))
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
}
