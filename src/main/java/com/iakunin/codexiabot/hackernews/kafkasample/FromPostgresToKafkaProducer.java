package com.iakunin.codexiabot.hackernews.kafkasample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iakunin.codexiabot.hackernews.repository.reactive.HackernewsItemRepository;
import com.iakunin.codexiabot.hackernews.repository.reactive.HackernewsItemRepositoryImpl;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

@Slf4j
public final class FromPostgresToKafkaProducer {

    private static final String TOPIC = "hackernews_item";

    private final KafkaSender<Integer, String> sender;

    private final ObjectMapper objectMapper;

    private final HackernewsItemRepository hackernewsItemRepository;

    public FromPostgresToKafkaProducer() {
        this.sender = KafkaSender.create(
            SenderOptions.create(
                new HashMap<>(){{
                    put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                    put(ProducerConfig.CLIENT_ID_CONFIG, "hackernews-item-producer");
                    put(ProducerConfig.ACKS_CONFIG, "all");
                    put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
                    put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                }}
            )
        );
        this.objectMapper = new ObjectMapper();
        this.hackernewsItemRepository = new HackernewsItemRepositoryImpl();
    }

    public void sendMessages() {
        this.sender.send(
            this.hackernewsItemRepository
                .findAll()
                .map(i ->
                    SenderRecord.create(
                        new ProducerRecord<>(
                            TOPIC,
                            Integer.valueOf(i.getExternalId()),
                            this.toBinary(i)
                        ),
                        Integer.valueOf(i.getExternalId())
                    )
                )
        )
        .doOnError(e -> log.error("Send failed", e))
        .subscribe();
    }

    public static void main(String[] args) throws Exception {
        new FromPostgresToKafkaProducer().sendMessages();

        new CountDownLatch(1).await();
    }

    private String toBinary(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
