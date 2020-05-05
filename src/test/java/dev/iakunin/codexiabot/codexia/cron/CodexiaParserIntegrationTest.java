package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
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
        WireMockServer.getInstance().stubFor(
            get(urlEqualTo("/recent.json?page=0"))
                .willReturn(ok()
                    .withHeader("Content-Type",  "application/json")
                    .withBody(
                        new TextOf(
                            new ResourceOf(
                                "wiremock/codexia/cron/codexia-parser/happyPath.json"
                            )
                        ).toString()
                    )
                )
        );
        WireMockServer.getInstance().stubFor(
            get(urlEqualTo("/recent.json?page=1"))
                .willReturn(ok()
                    .withHeader("Content-Type",  "application/json")
                    .withBody("[]")
                )
        );

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
                "app.codexia.base-url=" + WireMockServer.getInstance().baseUrl()
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
