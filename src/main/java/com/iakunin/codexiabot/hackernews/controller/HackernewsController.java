package com.iakunin.codexiabot.hackernews.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iakunin.codexiabot.hackernews.sdk.client.Hackernews;
import java.util.HashMap;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
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
public final class HackernewsController {

    private static final String TOPIC = "hackernews_item";

    private KafkaSender<Integer, String> sender;

    private ObjectMapper objectMapper;

    public HackernewsController() {
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

    private String toBinary(Object object) {
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
