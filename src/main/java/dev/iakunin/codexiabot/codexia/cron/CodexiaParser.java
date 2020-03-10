package dev.iakunin.codexiabot.codexia.cron;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import java.util.HashMap;
import java.util.List;
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

@Slf4j
@Component
public final class CodexiaParser {

    private static final String TOPIC = "codexia_project";

    private final CodexiaClient codexiaClient;
    private final CodexiaProjectRepository repository;
    private final ObjectMapper objectMapper;
    private final KafkaSender<Integer, String> sender;

    public CodexiaParser(
        CodexiaClient codexiaClient,
        CodexiaProjectRepository repository,
        ObjectMapper objectMapper,
        @Value("${app.kafka.bootstrap-servers}") String kafkaBootstrapServers
    ) {
        this.codexiaClient = codexiaClient;
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.sender = KafkaSender.create(
            SenderOptions.create(
                new HashMap<>(){{
                    put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
                    put(ProducerConfig.CLIENT_ID_CONFIG, "codexia-project-producer");
                    put(ProducerConfig.ACKS_CONFIG, "all");
                    put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
                    put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                }}
            )
        );
    }

    @Scheduled(cron="45 * * * * *") // every minute at 45th second
    public void run() {
        log.info("Running {}", this.getClass().getName());

        int page = 0;
        List<CodexiaClient.Project> projectList;

        do {
            projectList = this.codexiaClient.getItem(page).getBody();
            Objects.requireNonNull(projectList);

            projectList.stream()
                .filter(
                    project -> !this.repository.existsByExternalId(project.getId())
                )
                .map(
                    CodexiaProject.Factory::from
                )
                .forEach(
                    project -> {
                        log.info("Publishing to kafka codexiaProject with externalId='{}'", project.getExternalId());
                        this.sender.send(
                            Mono.just(
                                SenderRecord.create(
                                    new ProducerRecord<>(TOPIC, project.getExternalId(), this.toBinary(project)),
                                    project.getExternalId()
                                )
                            )
                        ).next().block();
                        log.info("CodexiaProject with externalId='{}' published to kafka", project.getExternalId());
                    }
                );

            page++;
        } while (!projectList.isEmpty());

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
