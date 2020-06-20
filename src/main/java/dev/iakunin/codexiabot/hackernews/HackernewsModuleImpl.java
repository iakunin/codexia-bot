package dev.iakunin.codexiabot.hackernews;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.factory.HackernewsItemFactory;
import dev.iakunin.codexiabot.hackernews.repository.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import feign.FeignException;
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
                try {
                    final HackernewsClient.Item item = this.getItem(id);
                    if (item.isDeleted()) {
                        this.updateEntities(id, item);
                    }
                } catch (final ItemNotFoundException | FeignException ex) {
                    log.warn("Exception occurred", ex);
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

    private void updateEntities(
        final Integer id,
        final HackernewsClient.Item item
    ) throws ItemNotFoundException {
        this.github.removeAllRepoSources(
            new GithubModule.DeleteArguments(
                GithubModule.Source.HACKERNEWS,
                String.valueOf(id)
            )
        );
        final HackernewsItem entity = this.repository
            .findByExternalId(id)
            .orElseThrow(
                () -> new ItemNotFoundException(
                    String.format("Unable to find HackernewsItem by externalId='%s'", id)
                )
            );
        HackernewsItemFactory.mutateEntity(entity, item);
        this.repository.save(entity);
    }

    @SuppressWarnings("PMD.FieldNamingConventions")
    private static final class ItemNotFoundException extends Exception {
        static final long serialVersionUID = -2287516993124229948L;

        ItemNotFoundException(final String message) {
            super(message);
        }
    }
}
