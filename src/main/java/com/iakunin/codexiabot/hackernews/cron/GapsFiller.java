package com.iakunin.codexiabot.hackernews.cron;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import com.iakunin.codexiabot.hackernews.repository.jpa.HackernewsItemRepository;
import com.iakunin.codexiabot.hackernews.repository.reactive.HackernewsItemRepositoryImpl;
import com.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

@Component
@Slf4j
public final class GapsFiller {

    private static final String TOPIC = "hackernews_item";

    private final HackernewsItemRepositoryImpl reactiveRepository;
    private final HackernewsItemRepository nonReactiveRepository;
    private final ObjectMapper objectMapper;
    private final KafkaSender<Integer, String> sender;

    public GapsFiller(
        ObjectMapper objectMapper,
        @Value("${app.kafka.bootstrap-servers}") String kafkaBootstrapServers,
        HackernewsItemRepositoryImpl reactiveRepository,
        HackernewsItemRepository nonReactiveRepository
    ) {
        this.reactiveRepository = reactiveRepository;
        this.nonReactiveRepository = nonReactiveRepository;
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

    @Scheduled(cron="0 0 * * * *") // every hour at 00 minutes and 00 seconds
    public void run() {
        log.info("GapsFiller run");

        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        final HttpClient httpClient = new HttpClient(sslContextFactory);
        httpClient.setMaxRequestsQueuedPerDestination(10000);
        ClientHttpConnector clientConnector = new JettyClientHttpConnector(httpClient);
        WebClient client = WebClient.builder().clientConnector(clientConnector).build();

        this.sender.send(
            this.reactiveRepository
                .findAbsentExternalIds(1, this.nonReactiveRepository.getMaxExternalId())
                .log(this.getClass().getName() + ".findAbsentExternalIds")
                .flatMap(
                    id -> client.get()
                        .uri(
                            String.format("https://hacker-news.firebaseio.com/v0/item/%s.json", id)
                        )
                        .retrieve()
                        .bodyToMono(HackernewsClient.Item.class)
                        .onErrorReturn(
                            new HackernewsClient.Item()
                                .setId(id)
                                .setType("")
                        )
                )
                .doOnError(e -> log.error("Getting from hacker-news is failed", e))
                .map(HackernewsItem.Factory::from)
                .map(i ->
                    SenderRecord.create(
                        new ProducerRecord<>(
                            TOPIC,
                            i.getExternalId(),
                            this.toBinary(i)
                        ),
                        i.getExternalId()
                    )
                )
        )
        .log(this.getClass().getName() + ".sendingToKafka")
        .doOnError(e -> log.error("Send failed", e))
        .blockLast();

        log.info("GapsFiller end");
    }

    private String toBinary(Object object) {
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
