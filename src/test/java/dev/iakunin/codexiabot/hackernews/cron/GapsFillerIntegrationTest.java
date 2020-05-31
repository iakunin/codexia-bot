package dev.iakunin.codexiabot.hackernews.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockWrapper;
import dev.iakunin.codexiabot.util.wiremock.Request;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class GapsFillerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private GapsFiller runnable;

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/gaps-filler/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/gaps-filler/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/gaps-filler/initial/oneMissing.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/gaps-filler/expected/oneMissing.yml")
    public void oneMissing() {
        new WireMockWrapper().stub(
            new Stub(
                new Request("/hackernews/item/2.json"),
                new Response(
                    new ResourceOf("wiremock/hackernews/cron/gaps-filler/2.json")
                )
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/hackernews/cron/gaps-filler/initial/twoMissing.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/cron/gaps-filler/expected/twoMissing.yml")
    public void twoMissing() {
        new WireMockWrapper().stub(
            new Stub(
                new Request("/hackernews/item/2.json"),
                new Response(
                    new ResourceOf("wiremock/hackernews/cron/gaps-filler/2.json")
                )
            )
        );
        new WireMockWrapper().stub(
            new Stub(
                new Request("/hackernews/item/4.json"),
                new Response(
                    new ResourceOf("wiremock/hackernews/cron/gaps-filler/4.json")
                )
            )
        );

        this.runnable.run();
    }
}
