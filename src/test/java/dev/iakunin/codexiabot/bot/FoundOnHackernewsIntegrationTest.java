package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class FoundOnHackernewsIntegrationTest extends AbstractIntegrationTest {

    @Qualifier("foundOnHackernews")
    @Autowired
    private Found foundOnHackernews;

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-hackernews/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-hackernews/expected/emptyDatabase.yml")
    void emptyDatabase() {
        foundOnHackernews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-hackernews/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-hackernews/expected/happyPath.yml")
    void happyPath() {
        WireMockServer.stub(
            new Stub(
                "/hackernews/item/99912.json",
                new ResourceOf("wiremock/bot/found-on-hackernews/withScore.json")
            )
        );

        foundOnHackernews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-hackernews/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-hackernews/expected/happyPathNoUpvotes.yml")
    void happyPathNoUpvotes() {
        WireMockServer.stub(
            new Stub(
                "/hackernews/item/99912.json",
                new ResourceOf("wiremock/bot/found-on-hackernews/withoutScore.json")
            )
        );

        foundOnHackernews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-hackernews/initial/reviewExists.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-hackernews/expected/reviewExists.yml")
    void reviewExists() {
        foundOnHackernews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-hackernews/initial/onlyCodexiaSource.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-hackernews/expected/onlyCodexiaSource.yml")
    void onlyCodexiaSource() {
        foundOnHackernews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-hackernews/initial/onlyHackernewsSource.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-hackernews/expected/onlyHackernewsSource.yml")
    void onlyHackernewsSource() {
        foundOnHackernews.run();
    }
}
