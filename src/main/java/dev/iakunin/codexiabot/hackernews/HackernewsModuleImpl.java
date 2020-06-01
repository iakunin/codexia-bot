package dev.iakunin.codexiabot.hackernews;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public final class HackernewsModuleImpl implements HackernewsModule {

    private final GithubModule github;

    private final HackernewsClient hackernews;

    private final HackernewsItemRepository repository;

    @Override
    public void healthCheckItems(final Stream<Integer> ids) {
        ids.forEach(
            id -> {
                final HackernewsClient.Item item = this.getItem(id);
                if (item.isDeleted()) {
                    this.updateEntities(id, item);
                }
            }
        );
    }

    @Override
    public HackernewsClient.Item getItem(final Integer id) {
        return
            Objects.requireNonNull(
                this.hackernews.getItem(id).getBody()
            );
    }

    private void updateEntities(final Integer id, final HackernewsClient.Item item) {
        this.github.removeAllRepoSources(
            new GithubModule.DeleteArguments(
                GithubModule.Source.HACKERNEWS,
                String.valueOf(id)
            )
        );
        final HackernewsItem entity = this.repository
            .findByExternalId(id)
            .orElseThrow(
                () -> new RuntimeException(
                    String.format("Unable to find HackernewsItem by externalId='%s'", id)
                )
            );
        HackernewsItem.Factory.mutateEntity(entity, item);
        this.repository.save(entity);
    }
}
