package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.config.GithubConfig;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    AbstractIntegrationTest.TestConfig.class,
    GithubConfig.class,
})
class MissingFillerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MissingFiller missingFiller;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/emptyDatabase.yml")
    void emptyDatabase() {
        missingFiller.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/oneWithGithubRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/oneWithGithubRepo.yml")
    void oneWithGithubRepo() {
        missingFiller.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/oneWithoutGithubRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/oneWithoutGithubRepo.yml")
    void oneWithoutGithubRepo() {
        WireMockServer.stub(
            new Stub(
                "/github/repos/instaloader/instaloader",
                new ResourceOf("wiremock/codexia/cron/missing-filler/instaloader.json")
            )
        );

        missingFiller.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/oneDeletedWithoutGithubRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/oneDeletedWithoutGithubRepo.yml")
    void oneDeletedWithoutGithubRepo() {
        missingFiller.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/twoWithoutGithubRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/twoWithoutGithubRepo.yml")
    void twoWithoutGithubRepo() {
        WireMockServer.stub(
            new Stub(
                "/github/repos/instaloader/instaloader",
                new ResourceOf("wiremock/codexia/cron/missing-filler/instaloader.json")
            )
        );
        WireMockServer.stub(
            new Stub(
                "/github/repos/arpit9667/algorithms-js",
                new ResourceOf("wiremock/codexia/cron/missing-filler/algorithms-js.json")
            )
        );

        missingFiller.run();
    }
}
