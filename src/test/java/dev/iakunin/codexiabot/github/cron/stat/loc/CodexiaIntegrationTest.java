package dev.iakunin.codexiabot.github.cron.stat.loc;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.tomakehurst.wiremock.client.WireMock;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Request;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = CodexiaIntegrationTest.Initializer.class)
public class CodexiaIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private Codexia linesOfCode;

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/codexia/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/codexia/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        linesOfCode.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/codexia/initial/processedRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/codexia/expected/processedRepo.yml")
    public void processedRepo() {
        new Stub(
            new Request(WireMock.urlPathEqualTo("/loc")),
            new Response(
                new ResourceOf("wiremock/github/cron/stat/loc/happyPath.json")
            )
        ).run();

        linesOfCode.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/codexia/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/codexia/expected/happyPath.yml")
    public void happyPath() {
        new Stub(
            new Request(WireMock.urlPathEqualTo("/loc")),
            new Response(
                new ResourceOf("wiremock/github/cron/stat/loc/happyPath.json")
            )
        ).run();

        linesOfCode.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/codexia/initial/tooManyRequests.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/codexia/expected/tooManyRequests.yml")
    public void tooManyRequests() {
        new Stub(
            new Request(WireMock.urlPathEqualTo("/loc")),
            new Response(HttpStatus.TOO_MANY_REQUESTS.value())
        ).run();

        linesOfCode.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/codexia/initial/codetabsException.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/codexia/expected/codetabsException.yml")
    public void codetabsException() {
        new Stub(
            new Request(WireMock.urlPathEqualTo("/loc")),
            new Response(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ).run();

        linesOfCode.run();
    }

    @AfterEach
    void after() {
        WireMockServer.getInstance().resetAll();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                "app.codetabs.base-url=" + WireMockServer.getInstance().baseUrl(),
                "app.github.service.lines-of-code.delay=0"
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
