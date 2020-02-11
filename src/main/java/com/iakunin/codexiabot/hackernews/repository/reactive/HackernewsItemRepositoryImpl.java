package com.iakunin.codexiabot.hackernews.repository.reactive;

import com.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public final class HackernewsItemRepositoryImpl implements HackernewsItemRepository {

    private final Mono<Connection> postgresqlConnection;

    public HackernewsItemRepositoryImpl() {
        ConnectionFactory connectionFactory = new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                //@TODO: There hardcoded values should be taken from application.properties
                .host("localhost")
                .port(54322)
                .username("codexia-bot")
                .password("codexia-bot")
                .database("codexia-bot")
                .options(
                    new HashMap<>() {{
                        put("lock_timeout", "10s");
                        put("statement_timeout", "0");
                    }}
                )
                .build()
        );

        this.postgresqlConnection = new ConnectionPool(
            ConnectionPoolConfiguration.builder(connectionFactory)
                .maxIdleTime(Duration.ofMillis(1000))
                .maxSize(500)
                .build()
        ).create();
    }

    @Override
    public Mono<Void> save(Flux<HackernewsItem> itemFlux) {
        return this.postgresqlConnection.flatMap(
            connection -> {
                final Mono<Void> publisher = (Mono<Void>) connection.beginTransaction();

                return publisher
                    .then(
                        itemFlux.flatMap(
                            item -> {
                                final LocalDateTime now = LocalDateTime.now();
                                return connection
                                    .createStatement(
                                        "INSERT INTO hackernews_item " +
                                        "(id, uuid, external_id, type, by, title, url, time, created_at, updated_at) " +
                                        "VALUES (DEFAULT, $1, $2, $3, $4, $5, $6, $7, $8, $9)"
                                    )
                                    .bind("$1", Optional.ofNullable(item.getUuid()).orElse(UUID.randomUUID()))
                                    .bind("$2", item.getExternalId())
                                    .bind("$3", item.getType())
                                    .bind("$4", Optional.ofNullable(item.getBy()).orElse(""))
                                    .bind("$5", Optional.ofNullable(item.getTitle()).orElse(""))
                                    .bind("$6", Optional.ofNullable(item.getUrl()).orElse(""))
                                    .bind("$7", Optional.ofNullable(item.getTime()).orElse(Instant.ofEpochSecond(0)))
                                    .bind("$8", Optional.ofNullable(item.getCreatedAt()).orElse(now))
                                    .bind("$9", Optional.ofNullable(item.getUpdatedAt()).orElse(now))
                                    .execute();
                            }
                        ).then()
                    ).then((Mono<Void>) connection.commitTransaction())
                    .then((Mono<Void>) connection.close())
                ;
            }
        );
    }

    @Override
    public Flux<HackernewsItem> findAll() {
        return this.postgresqlConnection.flatMapMany(
            connection -> {
                final Flux<? extends Result> publisher = (Flux<? extends Result>) connection
                    .createStatement("SELECT * FROM hackernews_item")
                    .execute();

                return publisher
                    .flatMap(
                        it -> it.map(
                            (row, rowMetadata) -> (HackernewsItem)
                                new HackernewsItem()
                                    .setExternalId(row.get("external_id", String.class))
                                    .setType(row.get("type", String.class))
                                    .setBy(row.get("by", String.class))
                                    .setTitle(row.get("title", String.class))
                                    .setUrl(row.get("url", String.class))
                                    .setTime(row.get("time", Instant.class))
                                    .setId(row.get("id", Long.class))
                                    .setUuid(row.get("uuid", UUID.class))
                                    .setCreatedAt(row.get("created_at", LocalDateTime.class))
                                    .setUpdatedAt(row.get("updated_at", LocalDateTime.class))
                        )
                    );
            }
        );
    }

    @Override
    public Flux<String> findAbsentExternalIds(String from, String to) {
        return this.postgresqlConnection.flatMapMany(
            connection -> {
                final Flux<? extends Result> publisher = (Flux<? extends Result>) connection
                    .createStatement(
                        "select gen.external_id::varchar from hackernews_item h " +
                            "right join ( " +
                            "    select generate_series as external_id " +
                            "    from generate_series($1, $2) " +
                            ") gen " +
                            "on gen.external_id::varchar = h.external_id " +
                            "where h.external_id is null"
                    )
                    .bind("$1", Integer.valueOf(from))
                    .bind("$2", Integer.valueOf(to))
                    .execute();

                return publisher
                    .flatMap(
                        it -> it.map(
                            (row, rowMetadata) -> row.get("external_id", String.class)
                        )
                    );
            }
        );
    }
}
