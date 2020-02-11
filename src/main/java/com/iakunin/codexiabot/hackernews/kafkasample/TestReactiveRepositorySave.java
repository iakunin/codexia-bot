package com.iakunin.codexiabot.hackernews.kafkasample;

import com.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import com.iakunin.codexiabot.hackernews.repository.reactive.HackernewsItemRepositoryImpl;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public final class TestReactiveRepositorySave {

    public static void main(String[] args) throws InterruptedException {
        //@TODO: move to integration tests
        Flux.just(
            createHackernewsItem("123456779991"),
            createHackernewsItem("123456779992"),
            createHackernewsItem("123456779993"),
            createHackernewsItem("123456779994"),
            createHackernewsItem("123456779995"),
            createHackernewsItem("123456779995"),
            createHackernewsItem("123456779995"),
            createHackernewsItem("123456779995"),
            createHackernewsItem("123456779995"),
            createHackernewsItem("123456779995"),
            createHackernewsItem("123456779995"),
            createHackernewsItem("123456779995"),
            createHackernewsItem("123456779995"),
            createHackernewsItem("123456779995"),
            createHackernewsItem("123456779996"),
            createHackernewsItem("123456779997"),
            createHackernewsItem("123456779998"),
            createHackernewsItem("123456779999"),
            createHackernewsItem("123456779999"),
            createHackernewsItem("123456779999")
        ).window(2)
        .flatMap(new HackernewsItemRepositoryImpl()::save)
        .subscribe();

        new CountDownLatch(1).await();
    }

    private static HackernewsItem createHackernewsItem(String s) {
        return new HackernewsItem()
            .setExternalId(s)
            .setType("test type")
            .setBy("TestBy")
            .setTitle("test title")
            .setUrl("test url")
            .setTime(Instant.now());
    }
}
