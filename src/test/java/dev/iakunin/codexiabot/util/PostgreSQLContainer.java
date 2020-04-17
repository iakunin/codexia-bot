package dev.iakunin.codexiabot.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.DriverManager;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.SneakyThrows;

public final class PostgreSQLContainer extends org.testcontainers.containers.PostgreSQLContainer<PostgreSQLContainer> {

    private static final Path LOG_FILE_PATH = Paths.get(System.getProperty("user.dir"), "var", "pg-container.log");

    private static final String IMAGE_VERSION = "postgres:11.5";

    private static PostgreSQLContainer CONTAINER;

    private PostgreSQLContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgreSQLContainer getInstance() {
        if (CONTAINER == null) {
            CONTAINER = new PostgreSQLContainer();
            CONTAINER.setCommand("postgres", "-c", "log_statement=all");
            CONTAINER.start();

            try {
                Files.deleteIfExists(LOG_FILE_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            CONTAINER.followOutput(
                frame -> {
                    try {
                        Files.write(
                            LOG_FILE_PATH,
                            frame.getBytes(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            );

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
