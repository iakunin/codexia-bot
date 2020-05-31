package dev.iakunin.codexiabot;

import com.github.database.rider.spring.api.DBRider;
import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.util.PostgresWrapper;
import dev.iakunin.codexiabot.util.WireMockWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dean.jraw.RedditClient;
import org.junit.jupiter.api.AfterEach;
import org.kohsuke.github.GitHub;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Slf4j
@SpringBootTest(classes = AbstractIntegrationTest.TestConfig.class)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
@DBRider
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "app.scheduling.enable=false",
    "spring.liquibase.enabled=false",
    "spring.main.allow-bean-definition-overriding=true",
    "app.github.service.lines-of-code.delay=0"
})
public abstract class AbstractIntegrationTest {

    @AfterEach
    public void after() {
        new WireMockWrapper().resetAll();
    }

    @Configuration
    @Import(CodexiaBotApplication.class)
    public static class TestConfig {
        @Bean
        public RedditClient redditClient() {
            return Mockito.mock(RedditClient.class);
        }

        @Bean
        public GitHub gitHub() {
            return Mockito.mock(GitHub.class);
        }

        @Bean
        public Faker faker() {
            return new Faker();
        }
    }

    static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(final ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                "app.database.host=" + new PostgresWrapper().getContainerIpAddress(),
                "app.database.port=" + new PostgresWrapper().getMappedPort(
                    PostgreSQLContainer.POSTGRESQL_PORT
                ),
                "app.database.name=" + new PostgresWrapper().getDatabaseName(),
                "spring.datasource.username=" + new PostgresWrapper().getUsername(),
                "spring.datasource.password=" + new PostgresWrapper().getPassword(),
                "app.codexia.base-url=" + new WireMockWrapper().baseUrl() + "/codexia",
                "app.codetabs.base-url=" + new WireMockWrapper().baseUrl() + "/codetabs",
                "app.hackernews.base-url=" + new WireMockWrapper().baseUrl() + "/hackernews"
            ).applyTo(context.getEnvironment());
        }
    }
}
