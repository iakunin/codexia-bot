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

class ProjectsHealthCheckIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectsHealthCheck projectsHealthCheck;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/projects-health-check/initial/noActiveProjectsInRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/projects-health-check/expected/noActiveProjectsInRepo.yml")
    void noActiveProjectsInRepo() {
        projectsHealthCheck.run();
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
                new Request(WireMock.urlPathMatching("/codexia/p/\\d+\\.json")),
                new Response(
                    new ResourceOf(
                        "wiremock/codexia/cron/projects-health-check/twoActiveProjectsInRepoButDeletedInCodexia.json"
                    )
                )
            )
        );

        projectsHealthCheck.run();
    }
}
