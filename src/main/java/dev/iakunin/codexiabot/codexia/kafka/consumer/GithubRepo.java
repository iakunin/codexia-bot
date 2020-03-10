package dev.iakunin.codexiabot.codexia.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.github.GithubModule;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@Component("codexia.kafka.consumer.GithubRepo")
@Slf4j
//@TODO: GithubRepo consumer can be replaced with generic class
public final class GithubRepo {

    private static final String TOPIC = "codexia_project";

    private final ObjectMapper objectMapper;

    private final GithubModule githubModule;

    public GithubRepo(
        ObjectMapper objectMapper,
        GithubModule githubModule,
        @Value("${app.kafka.bootstrap-servers}") String kafkaBootstrapServers
    ) {
        this.objectMapper = objectMapper;
        this.githubModule = githubModule;
        KafkaReceiver.create(
            ReceiverOptions.<Integer, String>create(
                    new HashMap<>(){{
                        //@TODO: move all there ConsumerConfigs to one place
                        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
                        put(ConsumerConfig.GROUP_ID_CONFIG, this.getClass().getName() + "-consumer");
                        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
                        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                        put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, Integer.MAX_VALUE);
                    }}
                )
                .commitBatchSize(1)
                .commitInterval(Duration.ofSeconds(1))
                .subscription(Collections.singleton(TOPIC))
            )
            //@TODO: rewrite via SpringRetry
            // https://objectpartners.com/2018/11/21/building-resilient-kafka-consumers-with-spring-retry/
            .receiveAutoAck()
            .concatMap(r -> r)
            .map(r -> {
                final CodexiaProject item = fromBinary(r.value(), CodexiaProject.class);
                log.info("Got an item from kafka with offset='{}'; item: {}", r.offset(), r.value());
                return item;
            })
            .doOnNext(
                i -> {
                    final String url = "https://github.com/" + i.getCoordinates();
                    try {
                        this.githubModule.createRepo(
                            new GithubModule.CreateArguments()
                                .setUrl(url)
                                .setSource(GithubModule.Source.CODEXIA)
                                .setExternalId(String.valueOf(i.getExternalId()))
                        );
                    } catch (RuntimeException | IOException e) {
                        log.info("Unable to create github repo; source url='{}'", url, e);
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
            return this.objectMapper.readValue(object, resultType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
