package dev.iakunin.codexiabot.hackernews.cron.healthcheck;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.tomakehurst.wiremock.client.WireMock;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockWrapper;
import dev.iakunin.codexiabot.util.wiremock.Request;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class AllItemsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AllItems runnable;

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/all-items/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/all-items/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/all-items/initial/oneActiveItem.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/all-items/expected/oneActiveItem.yml")
    public void oneActiveItem() {
        new WireMockWrapper().stub(
            new Stub(
                "/hackernews/item/2222.json",
                new ResourceOf("wiremock/hackernews/cron/health-check/activeItem.json")
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/all-items/initial/twoActiveItems.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/all-items/expected/twoActiveItems.yml")
    public void twoActiveItems() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(WireMock.urlPathMatching("/hackernews/item/\\d+\\.json")),
                new Response(
                    new ResourceOf("wiremock/hackernews/cron/health-check/activeItem.json")
                )
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/all-items/initial/oneDeletedItem.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/all-items/expected/oneDeletedItem.yml")
    public void oneDeletedItem() {
        new WireMockWrapper().stub(
            new Stub(
                "/hackernews/item/2222.json",
                new ResourceOf("wiremock/hackernews/cron/health-check/deletedItem.json")
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/all-items/initial/twoDeletedItems.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/health-check/all-items/expected/twoDeletedItems.yml")
    public void twoDeletedItems() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(WireMock.urlPathMatching("/hackernews/item/\\d+\\.json")),
                new Response(
                    new ResourceOf("wiremock/hackernews/cron/health-check/deletedItem.json")
                )
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/health-check/all-items/initial/hackernewsException.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/hackernews/cron/health-check/all-items/expected/hackernewsException.yml"
    )
    public void hackernewsException() {
        new WireMockWrapper().stub(
            new Stub(
                "/hackernews/item/2222.json",
                new Response(HttpStatus.INTERNAL_SERVER_ERROR.value())
            )
        );

        this.runnable.run();
    }
}
