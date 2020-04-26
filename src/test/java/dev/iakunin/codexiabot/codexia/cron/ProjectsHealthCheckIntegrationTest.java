package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
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
        WireMockServer.getInstance().stubFor(
            get(urlPathMatching("/p/\\d+\\.json"))
                .willReturn(ok()
                    .withHeader("Content-Type",  "application/json")
                    .withBody(
                        new TextOf(
                            new ResourceOf(
                                "wiremock/codexia/cron/projects-health-check/twoActiveProjectsInRepoButDeletedInCodexia.json"
                            )
                        ).toString()
                    )
                    .withTransformers(ResponseTemplateTransformer.NAME)
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
