package com.iakunin.codexiabot.hackernews.kafkasample;

import com.iakunin.codexiabot.hackernews.repository.reactive.HackernewsItemRepositoryImpl;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TestReactiveRepositoryAbsentExternalId {

    public static void main(String[] args) {
        //@TODO: move to integration tests
        Objects.requireNonNull(
            new HackernewsItemRepositoryImpl()
                .findAbsentExternalIds("1", "24031")
                .take(10)
                .collectList()
                .block()
        ).forEach(
            i -> log.info("ExternalId: {}", i)
        );
    }
}
