package dev.iakunin.codexiabot;

import com.github.database.rider.spring.api.DBRider;
import com.github.javafaker.Faker;
import dev.iakunin.codexiabot.container.PostgreSQLContainer;
import lombok.extern.slf4j.Slf4j;
import net.dean.jraw.RedditClient;
import org.junit.ClassRule;
import org.kohsuke.github.GitHub;
import static org.mockito.Mockito.mock;
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
    "spring.main.allow-bean-definition-overriding=true"
})
abstract public class AbstractIntegrationTest {

    @ClassRule
    public static PostgreSQLContainer CONTAINER = PostgreSQLContainer.getInstance();

    @Configuration
    @Import(CodexiaBotApplication.class)
    static class TestConfig {
        @Bean
        public RedditClient redditClient() {
            return mock(RedditClient.class);
        }

        @Bean
        public GitHub gitHub() {
            return mock(GitHub.class);
        }

        @Bean
        public Faker faker() {
            return new Faker();
        }
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "app.database.host=" + AbstractIntegrationTest.CONTAINER.getContainerIpAddress(),
                "app.database.port=" + AbstractIntegrationTest.CONTAINER.getMappedPort(
                    PostgreSQLContainer.POSTGRESQL_PORT
                ),
                "app.database.name=" + AbstractIntegrationTest.CONTAINER.getDatabaseName(),
                "spring.datasource.username=" + AbstractIntegrationTest.CONTAINER.getUsername(),
                "spring.datasource.password=" + AbstractIntegrationTest.CONTAINER.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
