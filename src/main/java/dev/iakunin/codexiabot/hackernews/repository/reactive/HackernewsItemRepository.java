package dev.iakunin.codexiabot.hackernews.repository.reactive;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HackernewsItemRepository {

    Mono<Void> save(Flux<HackernewsItem> itemFlux);

    Flux<HackernewsItem> findAll();

    Flux<Integer> findAbsentExternalIds(Integer from, Integer to);
}
