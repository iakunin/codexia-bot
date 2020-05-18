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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class WithoutLocIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WithoutLoc linesOfCode;

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/emptyDatabase.yml")
    void emptyDatabase() {
        linesOfCode.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/processedRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/processedRepo.yml")
    void processedRepo() {
        linesOfCode.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/loc/without-loc/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/loc/without-loc/expected/happyPath.yml")
    void happyPath() {
        WireMockServer.stub(
            new Stub(
                new Request(WireMock.urlPathEqualTo("/codetabs/loc")),
                new Response(
                    new ResourceOf("wiremock/github/cron/stat/loc/happyPath.json")
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
    void tooManyRequests() {
        WireMockServer.stub(
            new Stub(
                new Request(WireMock.urlPathEqualTo("/codetabs/loc")),
                new Response(HttpStatus.TOO_MANY_REQUESTS.value())
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
    void codetabsException() {
        WireMockServer.stub(
            new Stub(
                new Request(WireMock.urlPathEqualTo("/codetabs/loc")),
                new Response(HttpStatus.INTERNAL_SERVER_ERROR.value())
            )
        );

        linesOfCode.run();
    }
}
