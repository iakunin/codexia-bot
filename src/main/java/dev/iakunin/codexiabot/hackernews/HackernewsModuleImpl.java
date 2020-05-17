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
                try {
                    final HackernewsClient.Item item = this.getItem(externalId);
                    if (item.isDeleted()) {
                        this.updateEntities(externalId, item);
                    }
                } catch (Exception e) {
                    log.warn("Exception occurred", e);
                }
            }
        );
    }

    @Override
    public HackernewsClient.Item getItem(Integer id) {
        return
            Objects.requireNonNull(
                this.hackernewsClient.getItem(id).getBody()
            );
    }

    private void updateEntities(Integer externalId, HackernewsClient.Item item) {
        this.githubModule.removeAllRepoSources(
            new GithubModule.DeleteArguments(
                GithubModule.Source.HACKERNEWS,
                String.valueOf(externalId)
            )
        );
        final HackernewsItem hackernewsItem = this.hackernewsItemRepository
            .findByExternalId(externalId)
            .orElseThrow(
                () -> new RuntimeException(
                    String.format("Unable to find HackernewsItem by externalId='%s'", externalId)
                )
            );
        HackernewsItem.Factory.mutateEntity(hackernewsItem, item);
        this.hackernewsItemRepository.save(hackernewsItem);
    }
}
