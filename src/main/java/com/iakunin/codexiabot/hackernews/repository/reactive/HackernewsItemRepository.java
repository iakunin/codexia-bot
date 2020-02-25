package com.iakunin.codexiabot.hackernews.repository.reactive;

import com.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HackernewsItemRepository {

    Mono<Void> save(Flux<HackernewsItem> itemFlux);

    Flux<HackernewsItem> findAll();

    Flux<Integer> findAbsentExternalIds(Integer from, Integer to);
}
