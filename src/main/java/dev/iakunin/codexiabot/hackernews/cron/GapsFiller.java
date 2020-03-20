package dev.iakunin.codexiabot.hackernews.cron;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.jpa.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.repository.reactive.HackernewsItemRepositoryImpl;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
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
                new MapOf<>(
                    new MapEntry<>(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers),
                    new MapEntry<>(ProducerConfig.CLIENT_ID_CONFIG, "hackernews-item-producer"),
                    new MapEntry<>(ProducerConfig.ACKS_CONFIG, "all"),
                    new MapEntry<>(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class),
                    new MapEntry<>(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                )
            )
        );
    }

    @Scheduled(cron="${app.cron.hackernews.gaps-filler:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

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

        log.info("Exiting from {}", this.getClass().getName());
    }

    private String toBinary(Object object) {
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
