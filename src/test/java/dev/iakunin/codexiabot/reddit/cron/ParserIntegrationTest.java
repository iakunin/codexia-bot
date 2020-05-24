package dev.iakunin.codexiabot.reddit.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.tomakehurst.wiremock.client.WireMock;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.config.RedditConfig;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Request;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    AbstractIntegrationTest.TestConfig.class,
    RedditConfig.class,
})
public class ParserIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private Parser parser;

    @Test
    @DataSet(
        value = "db-rider/reddit/cron/parser/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/reddit/cron/parser/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        parser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/reddit/cron/parser/initial/notCodexiaSource.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/reddit/cron/parser/expected/notCodexiaSource.yml")
    public void notCodexiaSource() {
        parser.run();
    }

    @Test
    @DataSet(
        value = "db-rider/reddit/cron/parser/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/reddit/cron/parser/expected/happyPath.yml")
    public void happyPath() {
        WireMockServer.stub(
            new Stub(
                new Request(WireMock.urlMatching("/reddit/search.+")),
                new Response(
                    new ResourceOf("wiremock/reddit/cron/parser/reddit.json")
                )
            )
        );

        parser.run();
    }
}
