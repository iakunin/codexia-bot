package com.iakunin.codexiabot.hackernews.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iakunin.codexiabot.github.GithubModule;
import com.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

//@Component
@Slf4j
public final class GithubRepo {

    private static final String TOPIC = "hackernews_item";

    private ObjectMapper objectMapper;

    private GithubModule githubModule;

    public GithubRepo(ObjectMapper objectMapper, GithubModule githubModule) {
        this.objectMapper = objectMapper;
        this.githubModule = githubModule;
        KafkaReceiver.create(
            ReceiverOptions.<Integer, String>create(
                    new HashMap<>(){{
                        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                        put(ConsumerConfig.CLIENT_ID_CONFIG, "hackernews-item-consumer-1");
                        put(ConsumerConfig.GROUP_ID_CONFIG, "hackernews-item-consumer-1");
                        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
                        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                    }}
                ).subscription(Collections.singleton(TOPIC))
            )
            .receive()
            .map(r -> {
                final HackernewsItem item = fromBinary(r.value(), HackernewsItem.class);
                log.info("Got an item from kafka: {}", r.value());
                r.receiverOffset().acknowledge();
                return item;
            })
            .filter(item ->
                item.getUrl() != null &&
                item.getUrl().contains("github.com") &&
                !item.getUrl().contains("gist.github.com")
            )
            .doOnNext(
                i -> {
                    try {
                        this.githubModule.createRepo(
                            new GithubModule.Arguments()
                                .setUrl(i.getUrl())
                                .setSource(GithubModule.Source.HACKERNEWS)
                                .setExternalId(i.getExternalId())
                        );
                    } catch (RuntimeException|IOException e) {
                        log.info("Unable to create github repo; source url='{}'", i.getUrl(), e);
                    }
                }
            )
            .subscribe()
        ;
    }

    //@TODO: maybe it's possible to pass serializer for concrete type?
    //  this will help to get rid of the `fromBinary()` method
    private <T> T fromBinary(String object, Class<T> resultType) {
        try {
            return objectMapper.readValue(object, resultType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
