package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockServer;
import org.cactoos.text.Joined;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(initializers = ProjectsHealthCheckIntegrationTest.Initializer.class)
public class ProjectsHealthCheckIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectsHealthCheck projectsHealthCheck;

    @Test
    @Transactional
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
                        new Joined(
                            "\n",
                            // @todo #6 extract to json-file
                            "{",
                            "    \"id\": {{ replace request.requestLine.pathSegments.[1] '.json' '' }},",
                            "    \"coordinates\": \"test-project/test-repo\",",
                            "    \"platform\": \"github\",",
                            "    \"author\": \"iakunin-codexia-bot\",",
                            "    \"created\": \"2020-03-10 19:12:21 +0000\",",
                            "    \"deleted\": \"deleted by someone\"",
                            "}"
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
