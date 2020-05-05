package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.CodexiaBotApplication;
import dev.iakunin.codexiabot.util.WireMockServer;
import lombok.SneakyThrows;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
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
        this.wiremockWithResource(
            "/codexia/recent.json?page=0",
            "wiremock/codexia/cron/codexia-parser/codexia/recent.json"
        );
        this.wiremockWithBodyString("/codexia/recent.json?page=1", "[]");

        codexiaParser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/happyPathWithGithub.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/happyPathWithGithub.yml")
    public void happyPathWithGithub() {
        this.wiremockWithResource(
            "/codexia/recent.json?page=0",
            "wiremock/codexia/cron/codexia-parser/codexia/recent.json"
        );
        this.wiremockWithBodyString("/codexia/recent.json?page=1", "[]");
        this.wiremockWithResource(
            "/github/repos/casbin/casbin-rs",
            "wiremock/codexia/cron/codexia-parser/github/getRepo.json"
        );

        codexiaParser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/repoAlreadyExist.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/repoAlreadyExist.yml")
    public void repoAlreadyExist() {
        this.wiremockWithResource(
            "/codexia/recent.json?page=0",
            "wiremock/codexia/cron/codexia-parser/codexia/recent.json"
        );
        this.wiremockWithBodyString("/codexia/recent.json?page=1", "[]");
        this.wiremockWithResource(
            "/github/repos/casbin/casbin-rs",
            "wiremock/codexia/cron/codexia-parser/github/getRepo.json"
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
        this.wiremockWithResource(
            "/codexia/recent.json?page=0",
            "wiremock/codexia/cron/codexia-parser/codexia/recent.json"
        );
        this.wiremockWithBodyString("/codexia/recent.json?page=1", "[]");

        codexiaParser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/deletedProject.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/deletedProject.yml")
    public void deletedProject() {
        this.wiremockWithResource(
            "/codexia/recent.json?page=0",
            "wiremock/codexia/cron/codexia-parser/codexia/recentWithDeleted.json"
        );
        this.wiremockWithBodyString("/codexia/recent.json?page=1", "[]");

        codexiaParser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/notFoundInGithub.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/notFoundInGithub.yml")
    public void notFoundInGithub() {
        this.wiremockWithResource(
            "/codexia/recent.json?page=0",
            "wiremock/codexia/cron/codexia-parser/codexia/recent.json"
        );
        this.wiremockWithBodyString("/codexia/recent.json?page=1", "[]");
        WireMockServer.getInstance().stubFor(
            get(urlEqualTo("/github/repos/casbin/casbin-rs"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        new TextOf(
                            new ResourceOf(
                                "wiremock/codexia/cron/codexia-parser/github/repoNotFound.json"
                            )
                        ).toString()
                    )
                )
        );

        codexiaParser.run();
    }

    private void wiremockWithResource(String url, String resourcePath) {
        this.wiremockWithBodyString(
            url,
            new TextOf(
                new ResourceOf(
                    resourcePath
                )
            ).toString()
        );
    }

    private void wiremockWithBodyString(String url, String body) {
        WireMockServer.getInstance().stubFor(
            get(urlEqualTo(url))
                .willReturn(ok()
                    .withHeader("Content-Type", "application/json")
                    .withBody(body)
                )
        );
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
