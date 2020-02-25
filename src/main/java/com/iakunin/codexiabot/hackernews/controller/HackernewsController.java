package com.iakunin.codexiabot.hackernews.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iakunin.codexiabot.hackernews.repository.jpa.HackernewsItemRepository;
import com.iakunin.codexiabot.hackernews.sdk.client.Hackernews;
import java.util.HashMap;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

@Controller
@Slf4j
public final class HackernewsController {

    private static final String TOPIC = "hackernews_item";

    private KafkaSender<Integer, String> sender;

    private ObjectMapper objectMapper;

    private HackernewsItemRepository repository;

    private com.iakunin.codexiabot.hackernews.repository.reactive.HackernewsItemRepository  reactiveRepository;

    public HackernewsController(
        ObjectMapper objectMapper,
        HackernewsItemRepository repository,
        com.iakunin.codexiabot.hackernews.repository.reactive.HackernewsItemRepository reactiveRepository,
        @Value("${app.kafka.bootstrap-servers}") String kafkaBootstrapServers
    ) {
        this.objectMapper = objectMapper;
        this.repository = repository;
        this.reactiveRepository = reactiveRepository;
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

    @PostMapping("/api/v1/hackernews/item")
    @ResponseBody
    public ResponseEntity<String> putItemToKafka(
        @NotNull @Valid @RequestBody Hackernews.Item body
    ) {
        SenderRecord<Integer, String, Integer> message = SenderRecord.create(
            new ProducerRecord<>(TOPIC, toBinary(body)),
            1
        );

        this.sender.send(Mono.just(message)).next().block();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/api/v1/hackernews/sendAll")
    @ResponseBody
    public ResponseEntity<String> sendAll() {
        this.sender.send(
            this.reactiveRepository
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
        .blockLast();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String toBinary(Object object) {
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
