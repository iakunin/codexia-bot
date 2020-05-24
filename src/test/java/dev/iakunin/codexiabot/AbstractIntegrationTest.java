package dev.iakunin.codexiabot;

import com.github.database.rider.spring.api.DBRider;
import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.util.PostgreSQLContainer;
import dev.iakunin.codexiabot.util.WireMockServer;
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

@Slf4j
@SpringBootTest(classes = { AbstractIntegrationTest.TestConfig.class })
@ContextConfiguration(initializers = { AbstractIntegrationTest.Initializer.class })
@DBRider
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "app.scheduling.enable=false",
    "spring.liquibase.enabled=false",
    "spring.main.allow-bean-definition-overriding=true",
    "app.github.service.lines-of-code.delay=0",
})
abstract public class AbstractIntegrationTest {

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

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "app.database.host=" + PostgreSQLContainer.getInstance().getContainerIpAddress(),
                "app.database.port=" + PostgreSQLContainer.getInstance().getMappedPort(
                    PostgreSQLContainer.POSTGRESQL_PORT
                ),
                "app.database.name=" + PostgreSQLContainer.getInstance().getDatabaseName(),
                "spring.datasource.username=" + PostgreSQLContainer.getInstance().getUsername(),
                "spring.datasource.password=" + PostgreSQLContainer.getInstance().getPassword(),
                "app.codexia.base-url=" + WireMockServer.getInstance().baseUrl() + "/codexia",
                "app.codetabs.base-url=" + WireMockServer.getInstance().baseUrl() + "/codetabs",
                "app.hackernews.base-url=" + WireMockServer.getInstance().baseUrl() + "/hackernews"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @AfterEach
    public void after() {
        WireMockServer.getInstance().resetAll();
    }
}
