package dev.iakunin.codexiabot.util;

import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
import org.testcontainers.containers.PostgreSQLContainer;

public final class PostgresWrapper {

    private static final Unchecked<PostgreSQLContainer<?>> CONTAINER = new Unchecked<>(
        new Sticky<>(
            () -> {
                final var container = new PostgreSQLContainer<>("postgres:12.2");
                container.start();
                new Migrations(container).run();
                return container;
            }
        )
    );

    public String getContainerIpAddress() {
        return CONTAINER.value().getContainerIpAddress();
    }

    public Integer getMappedPort(final Integer original) {
        return CONTAINER.value().getMappedPort(original);
    }

    public String getDatabaseName() {
        return CONTAINER.value().getDatabaseName();
    }

    public String getUsername() {
        return CONTAINER.value().getUsername();
    }

    public String getPassword() {
        return CONTAINER.value().getPassword();
    }

    private static class Migrations {

        private final PostgreSQLContainer<?> container;

        Migrations(final PostgreSQLContainer<?> container) {
            this.container = container;
        }

        public void run() throws LiquibaseException, SQLException {
            final Liquibase liquibase = new Liquibase(
                "db/changelog/db.changelog-master.yaml",
                new ClassLoaderResourceAccessor(),
                DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                    new JdbcConnection(
                        DriverManager.getConnection(
                            this.container.getJdbcUrl(),
                            this.container.getUsername(),
                            this.container.getPassword()
                        )
                    )
                )
            );
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }
}
