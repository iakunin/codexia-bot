package dev.iakunin.codexiabot.hackernews.cron.healthcheck;

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
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = CodexiaItemsIntegrationTest.Initializer.class)
public class CodexiaItemsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CodexiaItems codexiaItems;

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/codexia-items/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/codexia-items/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        codexiaItems.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/codexia-items/initial/withoutCodexiaSource.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/codexia-items/expected/withoutCodexiaSource.yml")
    public void withoutCodexiaSource() {
        codexiaItems.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/codexia-items/initial/oneActiveItem.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/codexia-items/expected/oneActiveItem.yml")
    public void oneActiveItem() {
        WireMockServer.stub(
            new Stub(
                "/item/2222.json",
                new ResourceOf("wiremock/hackernews/cron/health-check/activeItem.json")
            )
        );

        codexiaItems.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/codexia-items/initial/twoActiveItems.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/codexia-items/expected/twoActiveItems.yml")
    public void twoActiveItems() {
        WireMockServer.stub(
            new Stub(
                new Request(WireMock.urlPathMatching("/item/\\d+\\.json")),
                new Response(
                    new ResourceOf("wiremock/hackernews/cron/health-check/activeItem.json")
                )
            )
        );

        codexiaItems.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/codexia-items/initial/oneDeletedItem.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/codexia-items/expected/oneDeletedItem.yml")
    public void oneDeletedItem() {
        WireMockServer.stub(
            new Stub(
                "/item/2222.json",
                new ResourceOf("wiremock/hackernews/cron/health-check/deletedItem.json")
            )
        );

        codexiaItems.run();
    }

    @AfterEach
    void after() {
        WireMockServer.getInstance().resetAll();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                "app.hackernews.base-url=" + WireMockServer.getInstance().baseUrl()
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
