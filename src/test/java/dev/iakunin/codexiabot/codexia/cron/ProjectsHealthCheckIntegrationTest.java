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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = ProjectsHealthCheckIntegrationTest.Initializer.class)
public class ProjectsHealthCheckIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectsHealthCheck projectsHealthCheck;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/projects-health-check/initial/noActiveProjectsInRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/projects-health-check/expected/noActiveProjectsInRepo.yml")
    public void noActiveProjectsInRepo() {
        projectsHealthCheck.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/projects-health-check/initial/twoActiveProjectsInRepoButDeletedInCodexia.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/projects-health-check/expected/twoActiveProjectsInRepoButDeletedInCodexia.yml")
    public void twoActiveProjectsInRepoButDeletedInCodexia() {
        WireMockServer.stub(
            new Stub(
                new Request(WireMock.urlPathMatching("/p/\\d+\\.json")),
                new Response(
                    new ResourceOf(
                        "wiremock/codexia/cron/projects-health-check/twoActiveProjectsInRepoButDeletedInCodexia.json"
                    )
                )
            )
        );

        projectsHealthCheck.run();
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
