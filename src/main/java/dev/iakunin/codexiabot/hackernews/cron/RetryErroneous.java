package dev.iakunin.codexiabot.hackernews.cron;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import dev.iakunin.codexiabot.hackernews.repository.jpa.HackernewsItemRepository;
import dev.iakunin.codexiabot.hackernews.sdk.HackernewsClient;
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
public final class RetryErroneous {

    private static final String TOPIC = "hackernews_item";

    private final HackernewsItemRepository hackernewsItemRepository;
    private final HackernewsClient hackernewsClient;
    private final ObjectMapper objectMapper;
    private final KafkaSender<Integer, String> sender;

    public RetryErroneous(
        HackernewsItemRepository hackernewsItemRepository,
        HackernewsClient hackernewsClient,
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
                    put(ProducerConfig.CLIENT_ID_CONFIG, this.getClass().getName() + "-producer");
                    put(ProducerConfig.ACKS_CONFIG, "all");
                    put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
                    put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                }}
            )
        );
    }

    @Scheduled(cron="${app.cron.hackernews.retry-erroneous:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.hackernewsItemRepository.findAllByType("")
            .forEach(
                repo -> {
                    try {
                        log.info("Trying to get item with externalId='{}'", repo.getExternalId());
                        final HackernewsClient.Item item = this.hackernewsClient.getItem(repo.getExternalId()).getBody();
                        Objects.requireNonNull(item);
                        log.info("Successfully got item with externalId='{}'; {}", repo.getExternalId(), item);

                        HackernewsItem.Factory.mutateEntity(repo, item);

                        log.info("Trying to save to DB; {}", repo);
                        this.hackernewsItemRepository.save(repo);
                        log.info("Successfully saved to DB; {}", repo);

                        log.info("Trying to publish to kafka; {}", item);
                        this.sender.send(
                            Mono.just(
                                SenderRecord.create(
                                    new ProducerRecord<>(TOPIC, repo.getExternalId(), this.toBinary(repo)),
                                    repo.getExternalId()
                                )
                            )
                        ).next().block();
                    } catch (Exception e) {
                        log.warn("Exception occurred during getting hackernews item '{}'", repo.getExternalId(), e);
                    }
                }
            );

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
