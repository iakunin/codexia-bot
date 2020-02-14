package com.iakunin.codexiabot.hackernews.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iakunin.codexiabot.hackernews.sdk.client.Hackernews;
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

    public GithubRepo() {
        this.objectMapper = new ObjectMapper();
        KafkaReceiver.create(
            ReceiverOptions.<Integer, String>create(
                new HashMap<>(){{
                    put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                    put(ConsumerConfig.CLIENT_ID_CONFIG, "hackernews-item-consumer-1");
                    put(ConsumerConfig.GROUP_ID_CONFIG, "hackernews-item-consumer");
                    put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
                    put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                }}
            ).subscription(Collections.singleton(TOPIC))
        ).receive()
        .doOnNext(r -> {
            //@TODO:
            //  1. Filter url by regexp (is 'github.com', but not 'gist.github.com')
            //  2. Check this repo existence in GitHub-api
            //  3. If it is, build GitHubRepo DTO and push to `github_repo` kafka-topic
            //  4. Consume `github_repo` kafka-topic
            //     - save
            //     - calculate some heavy analytics (like project-size in LOC)
            log.info("Hackernews.Item: {}", fromBinary(r.value(), Hackernews.Item.class));

            r.receiverOffset().acknowledge();
        })
        .subscribe();
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
