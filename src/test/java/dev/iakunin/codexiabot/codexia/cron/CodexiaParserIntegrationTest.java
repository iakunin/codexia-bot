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
public class CodexiaParserIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CodexiaParser parser;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/happyPathWithoutGithub.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/happyPathWithoutGithub.yml")
    public void happyPathWithoutGithub() {
        new WireMockWrapper().stub(
            new Stub(
                "/codexia/recent.json?page=0",
                new ResourceOf("wiremock/codexia/cron/codexia-parser/codexia/recent.json")
            )
        );
        new WireMockWrapper().stub(new Stub("/codexia/recent.json?page=1", "[]"));

        this.parser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/happyPathWithGithub.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/happyPathWithGithub.yml")
    public void happyPathWithGithub() {
        new WireMockWrapper().stub(
            new Stub(
                "/codexia/recent.json?page=0",
                new ResourceOf("wiremock/codexia/cron/codexia-parser/codexia/recent.json")
            )
        );
        new WireMockWrapper().stub(new Stub("/codexia/recent.json?page=1", "[]"));
        new WireMockWrapper().stub(
            new Stub(
                "/github/repos/casbin/casbin-rs",
                new ResourceOf("wiremock/codexia/cron/codexia-parser/github/getRepo.json")
            )
        );

        this.parser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/codexiaProjectAlreadyExist.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/codexiaProjectAlreadyExist.yml")
    public void codexiaProjectAlreadyExist() {
        new WireMockWrapper().stub(
            new Stub(
                "/codexia/recent.json?page=0",
                new ResourceOf("wiremock/codexia/cron/codexia-parser/codexia/recent.json")
            )
        );
        new WireMockWrapper().stub(new Stub("/codexia/recent.json?page=1", "[]"));

        this.parser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/codexia-parser/initial/deletedProject.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/codexia-parser/expected/deletedProject.yml")
    public void deletedProject() {
        new WireMockWrapper().stub(
            new Stub(
                "/codexia/recent.json?page=0",
                new ResourceOf(
                    "wiremock/codexia/cron/codexia-parser/codexia/recentWithDeleted.json"
                )
            )
        );
        new WireMockWrapper().stub(new Stub("/codexia/recent.json?page=1", "[]"));

        this.parser.run();
    }
}
