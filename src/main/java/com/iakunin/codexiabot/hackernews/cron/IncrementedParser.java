package com.iakunin.codexiabot.hackernews.cron;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import com.iakunin.codexiabot.hackernews.repository.jpa.HackernewsItemRepository;
import com.iakunin.codexiabot.hackernews.sdk.client.Hackernews;
import java.util.HashMap;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

@Component
@Slf4j
public final class IncrementedParser {

    private static final String TOPIC = "hackernews_item";

    private final HackernewsItemRepository hackernewsItemRepository;

    private final Hackernews hackernewsClient;

    private final ObjectMapper objectMapper;

    private final KafkaSender<Integer, String> sender;

    public IncrementedParser(
        HackernewsItemRepository hackernewsItemRepository,
        Hackernews hackernewsClient,
        ObjectMapper objectMapper,
        @Value("${app.kafka.bootstrap-servers}") String kafkaBootstrapServers
    ) {
        this.hackernewsItemRepository = hackernewsItemRepository;
        this.hackernewsClient = hackernewsClient;
        this.objectMapper = objectMapper;
        this.sender = KafkaSender.create(
            SenderOptions.create(
                new HashMap<>(){{
                    put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
                    put(ProducerConfig.CLIENT_ID_CONFIG, "hackernews-item-producer");
                    put(ProducerConfig.ACKS_CONFIG, "all");
                    put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
                    put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                }}
            )
        );
    }

    @Scheduled(cron="5 * * * * *") // every minute at 05th seconds
    //@TODO: add ShedLock: https://www.baeldung.com/shedlock-spring
    public void run() {
        log.info("IncrementedParser run");

        int currentExternalId = this.hackernewsItemRepository.getMaxExternalId() + 1;

        for (int errorsCount = 0; errorsCount <= 10; currentExternalId++){
            try {
                log.info("Trying to get item with externalId='{}'", currentExternalId);
                final Hackernews.Item item = this.hackernewsClient.getItem(currentExternalId).getBody();
                Objects.requireNonNull(item);

                log.info("Successfully got item with externalId='{}'; trying to publish to kafka; {}", currentExternalId, item);
                final HackernewsItem hackernewsItem = HackernewsItem.Factory.from(item);
                this.sender.send(
                    Mono.just(
                        SenderRecord.create(
                            new ProducerRecord<>(TOPIC, hackernewsItem.getExternalId(), this.toBinary(hackernewsItem)),
                            hackernewsItem.getExternalId()
                        )
                    )
                ).next().block();

                log.info("Item with externalId='{}' successfully published to kafka ", currentExternalId);
            } catch (Exception e) {
                log.warn("Exception occurred during getting hackernews item '{}'", currentExternalId, e);
                errorsCount++;
            }
        }

        log.info("IncrementedParser end");
    }

    private String toBinary(Object object) {
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}