package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.CodexiaBotApplication;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import lombok.SneakyThrows;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = {
    AbstractIntegrationTest.TestConfig.class,
    CodexiaParserIntegrationTest.TestConfig.class,
})
@ContextConfiguration(initializers = CodexiaParserIntegrationTest.Initializer.class)
public class CodexiaParserIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CodexiaParser codexiaParser;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/happyPathWithoutGithub.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/happyPathWithoutGithub.yml")
    public void happyPathWithoutGithub() {
        WireMockServer.stub(
            new Stub(
                "/codexia/recent.json?page=0",
                new ResourceOf("wiremock/codexia/cron/codexia-parser/codexia/recent.json")
            )
        );
        WireMockServer.stub(new Stub("/codexia/recent.json?page=1", "[]"));

        codexiaParser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/happyPathWithGithub.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/happyPathWithGithub.yml")
    public void happyPathWithGithub() {
        WireMockServer.stub(
            new Stub(
                "/codexia/recent.json?page=0",
                new ResourceOf("wiremock/codexia/cron/codexia-parser/codexia/recent.json")
            )
        );
        WireMockServer.stub(new Stub("/codexia/recent.json?page=1", "[]"));
        WireMockServer.stub(
            new Stub(
                "/github/repos/casbin/casbin-rs",
                new ResourceOf("wiremock/codexia/cron/codexia-parser/github/getRepo.json")
            )
        );

        codexiaParser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/codexiaProjectAlreadyExist.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/codexiaProjectAlreadyExist.yml")
    public void codexiaProjectAlreadyExist() {
        WireMockServer.stub(
            new Stub(
                "/codexia/recent.json?page=0",
                new ResourceOf("wiremock/codexia/cron/codexia-parser/codexia/recent.json")
            )
        );
        WireMockServer.stub(new Stub("/codexia/recent.json?page=1", "[]"));

        codexiaParser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/deletedProject.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/deletedProject.yml")
    public void deletedProject() {
        WireMockServer.stub(
            new Stub(
                "/codexia/recent.json?page=0",
                new ResourceOf("wiremock/codexia/cron/codexia-parser/codexia/recentWithDeleted.json")
            )
        );
        WireMockServer.stub(new Stub("/codexia/recent.json?page=1", "[]"));

        codexiaParser.run();
    }

    @AfterEach
    void after() {
        WireMockServer.getInstance().resetAll();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                "app.codexia.base-url=" + WireMockServer.getInstance().baseUrl() + "/codexia"
            ).applyTo(applicationContext.getEnvironment());
        }
    }

    @Configuration
    @Import(CodexiaBotApplication.class)
    static class TestConfig {
        @SneakyThrows
        @Bean
        public GitHub gitHub() {
            return new GitHubBuilder()
                .withEndpoint(
                    WireMockServer.getInstance().baseUrl() + "/github"
                ).build();
        }
    }
}
