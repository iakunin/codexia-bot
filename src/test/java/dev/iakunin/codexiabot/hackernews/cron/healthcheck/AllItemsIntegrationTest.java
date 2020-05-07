package dev.iakunin.codexiabot.hackernews.cron.healthcheck;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = AllItemsIntegrationTest.Initializer.class)
public class AllItemsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AllItems allItems;

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/all-items/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/all-items/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        allItems.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/all-items/initial/oneActiveItem.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/all-items/expected/oneActiveItem.yml")
    public void oneActiveItem() {
        WireMockServer.stub(
            new Stub(
                "/item/2222.json",
                new ResourceOf("wiremock/hackernews/cron/health-check/all-items/oneActiveItem.json")
            )
        );

        allItems.run();
    }

//    @Test
//    @DataSet(
//        value = "db-rider/hackernews/cron/health-check/all-items/initial/oneDeletedItem.yml",
//        cleanBefore = true, cleanAfter = true
//    )
//    @ExpectedDataSet("db-rider/hackernews/cron/health-check/all-items/expected/oneDeletedItem.yml")
//    public void oneDeletedItem() {
//
//        // @todo wiremock
//
//        allItems.run();
//    }

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
