package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.CodexiaBotApplication;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import lombok.SneakyThrows;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = {
    AbstractIntegrationTest.TestConfig.class,
    MissingFillerIntegrationTest.TestConfig.class,
})
public class MissingFillerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MissingFiller missingFiller;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        missingFiller.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/oneWithGithubRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/oneWithGithubRepo.yml")
    public void oneWithGithubRepo() {
        missingFiller.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/oneWithoutGithubRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/oneWithoutGithubRepo.yml")
    public void oneWithoutGithubRepo() {
        WireMockServer.stub(
            new Stub(
                "/repos/instaloader/instaloader",
                new ResourceOf("wiremock/codexia/cron/missing-filler/instaloader.json")
            )
        );

        missingFiller.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/twoWithoutGithubRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/twoWithoutGithubRepo.yml")
    public void twoWithoutGithubRepo() {
        WireMockServer.stub(
            new Stub(
                "/repos/instaloader/instaloader",
                new ResourceOf("wiremock/codexia/cron/missing-filler/instaloader.json")
            )
        );
        WireMockServer.stub(
            new Stub(
                "/repos/arpit9667/algorithms-js",
                new ResourceOf("wiremock/codexia/cron/missing-filler/algorithms-js.json")
            )
        );

        missingFiller.run();
    }

    @AfterEach
    void after() {
        WireMockServer.getInstance().resetAll();
    }

    @Configuration
    @Import(CodexiaBotApplication.class)
    static class TestConfig {
        @SneakyThrows
        @Bean
        public GitHub gitHub() {
            return new GitHubBuilder()
                .withEndpoint(
                    WireMockServer.getInstance().baseUrl()
                ).build();
        }
    }
}
