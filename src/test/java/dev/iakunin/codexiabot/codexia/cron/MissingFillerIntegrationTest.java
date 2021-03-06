package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.config.GithubConfig;
import dev.iakunin.codexiabot.util.WireMockWrapper;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@SpringBootTest(classes = {
    AbstractIntegrationTest.TestConfig.class,
    GithubConfig.class
})
public class MissingFillerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MissingFiller runnable;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/oneWithGithubRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/oneWithGithubRepo.yml")
    public void oneWithGithubRepo() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/oneWithoutGithubRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/oneWithoutGithubRepo.yml")
    public void oneWithoutGithubRepo() {
        new WireMockWrapper().stub(
            new Stub(
                "/github/repos/instaloader/instaloader",
                new ResourceOf("wiremock/codexia/cron/missing-filler/instaloader.json")
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/oneDeletedWithoutGithubRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/missing-filler/expected/oneDeletedWithoutGithubRepo.yml"
    )
    public void oneDeletedWithoutGithubRepo() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/missing-filler/initial/twoWithoutGithubRepo.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/missing-filler/expected/twoWithoutGithubRepo.yml")
    public void twoWithoutGithubRepo() {
        new WireMockWrapper().stub(
            new Stub(
                "/github/repos/instaloader/instaloader",
                new ResourceOf("wiremock/codexia/cron/missing-filler/instaloader.json")
            )
        );
        new WireMockWrapper().stub(
            new Stub(
                "/github/repos/arpit9667/algorithms-js",
                new ResourceOf("wiremock/codexia/cron/missing-filler/algorithms-js.json")
            )
        );

        this.runnable.run();
    }
}
