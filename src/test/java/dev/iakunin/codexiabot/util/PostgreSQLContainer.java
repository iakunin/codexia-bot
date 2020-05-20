package dev.iakunin.codexiabot.util;

import java.sql.DriverManager;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.SneakyThrows;

public final class PostgreSQLContainer extends org.testcontainers.containers.PostgreSQLContainer<PostgreSQLContainer> {

    private static final String IMAGE_VERSION = "postgres:12.2";

    private static PostgreSQLContainer CONTAINER;

    private PostgreSQLContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgreSQLContainer getInstance() {
        if (CONTAINER == null) {
            CONTAINER = new PostgreSQLContainer();
            CONTAINER.start();
            CONTAINER.runMigrations();
            CONTAINER.waitUntilContainerStarted();
        }

        return CONTAINER;
    }

    @SneakyThrows
    private void runMigrations() {
        final Liquibase liquibase = new Liquibase(
            "db/changelog/db.changelog-master.yaml",
            new ClassLoaderResourceAccessor(),
            DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                new JdbcConnection(
                    DriverManager.getConnection(
                        CONTAINER.getJdbcUrl(),
                        CONTAINER.getUsername(),
                        CONTAINER.getPassword()
                    )
                )
            )
        );
        liquibase.update(new Contexts(), new LabelExpression());
    }
}
