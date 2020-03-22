package dev.iakunin.codexiabot.container;

import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.SneakyThrows;

public final class PostgreSQLContainer extends org.testcontainers.containers.PostgreSQLContainer<PostgreSQLContainer> {

    private static final String IMAGE_VERSION = "postgres:11.5";
    private static PostgreSQLContainer CONTAINER;

    private PostgreSQLContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgreSQLContainer getInstance() {
        if (CONTAINER == null) {
            CONTAINER = new PostgreSQLContainer();
            CONTAINER.start();
            CONTAINER.waitUntilContainerStarted();
        }

        return CONTAINER;
    }

    @SneakyThrows
    @Override
    public void start() {
        super.start();
        this.runMigrations();
    }

    private void runMigrations() throws SQLException, LiquibaseException {
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
