package dev.iakunin.codexiabot.codexia.cron;

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

class UpdateProjectsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UpdateProjects cron;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/projects-health-check/initial/noActiveProjectsInRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/projects-health-check/expected/noActiveProjectsInRepo.yml")
    void noActiveProjectsInRepo() {
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/projects-health-check/initial/twoActiveProjectsInRepoButDeletedInCodexia.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/projects-health-check/expected/twoActiveProjectsInRepoButDeletedInCodexia.yml")
    void twoActiveProjectsInRepoButDeletedInCodexia() {
        WireMockServer.stub(
            new Stub(
                new Request(WireMock.urlPathEqualTo("/codexia/p/12.json")),
                new Response(
                    new ResourceOf(
                        "wiremock/codexia/cron/projects-health-check/12deletedWithBadges.json"
                    )
                )
            )
        );
        WireMockServer.stub(
            new Stub(
                new Request(WireMock.urlPathEqualTo("/codexia/p/34.json")),
                new Response(
                    new ResourceOf(
                        "wiremock/codexia/cron/projects-health-check/34deletedWithBadges.json"
                    )
                )
            )
        );

        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/projects-health-check/initial/oneSuccessAfterOneException.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/projects-health-check/expected/oneSuccessAfterOneException.yml")
    void oneSuccessAfterOneException() {
        WireMockServer.stub(
            new Stub(
                new Request(WireMock.urlPathEqualTo("/codexia/p/12.json")),
                new Response(HttpStatus.INTERNAL_SERVER_ERROR.value())
            )
        );
        WireMockServer.stub(
            new Stub(
                new Request(WireMock.urlPathEqualTo("/codexia/p/34.json")),
                new Response(
                    new ResourceOf(
                        "wiremock/codexia/cron/projects-health-check/34deletedWithBadges.json"
                    )
                )
            )
        );

        cron.run();
    }
}
