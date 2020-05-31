package dev.iakunin.codexiabot.github.cron.stat.loc;

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
public class WithoutLocIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WithoutLoc runnable;

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/processedRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/processedRepo.yml")
    public void processedRepo() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/happyPath.yml")
    public void happyPath() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(WireMock.urlPathEqualTo("/codetabs/loc")),
                new Response(
                    new ResourceOf("wiremock/github/cron/stat/loc/happyPath.json")
                )
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/tooManyRequests.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/tooManyRequests.yml")
    public void tooManyRequests() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(WireMock.urlPathEqualTo("/codetabs/loc")),
                new Response(HttpStatus.TOO_MANY_REQUESTS.value())
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/codetabsException.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/codetabsException.yml")
    public void codetabsException() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(WireMock.urlPathEqualTo("/codetabs/loc")),
                new Response(HttpStatus.INTERNAL_SERVER_ERROR.value())
            )
        );

        this.runnable.run();
    }
}
