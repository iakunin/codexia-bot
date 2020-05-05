package dev.iakunin.codexiabot.github.cron.stat.loc;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockServer;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = WithoutLocIntegrationTest.Initializer.class)
public class WithoutLocIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WithoutLoc linesOfCode;

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        linesOfCode.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/processedRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/processedRepo.yml")
    public void processedRepo() {
        linesOfCode.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/happyPath.yml")
    public void happyPath() {
        WireMockServer.getInstance().stubFor(
            get(WireMock.urlPathEqualTo("/loc"))
                .willReturn(ok()
                    .withHeader("Content-Type",  "application/json")
                    .withBody(
                        new TextOf(
                            new ResourceOf(
                                "wiremock/github/cron/stat/loc/happyPath.json"
                            )
                        ).toString()
                    )
                )
        );

        linesOfCode.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/tooManyRequests.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/tooManyRequests.yml")
    public void tooManyRequests() {
        WireMockServer.getInstance().stubFor(
            get(WireMock.urlPathEqualTo("/loc"))
                .willReturn(aResponse()
                    .withStatus(429)
                )
        );

        linesOfCode.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/codetabsException.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/codetabsException.yml")
    public void codetabsException() {
        WireMockServer.getInstance().stubFor(
            get(WireMock.urlPathEqualTo("/loc"))
                .willReturn(aResponse()
                    .withStatus(500)
                )
        );

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
