package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class FoundOnRedditIntegrationTest extends AbstractIntegrationTest {

    @Qualifier("foundOnReddit")
    @Autowired
    private Found foundOnReddit;

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-reddit/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-reddit/expected/emptyDatabase.yml")
    void emptyDatabase() {
        foundOnReddit.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-reddit/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-reddit/expected/happyPath.yml")
    void happyPath() {
        foundOnReddit.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-reddit/initial/reviewExists.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-reddit/expected/reviewExists.yml")
    void reviewExists() {
        foundOnReddit.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-reddit/initial/onlyCodexiaSource.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-reddit/expected/onlyCodexiaSource.yml")
    void onlyCodexiaSource() {
        foundOnReddit.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-reddit/initial/onlyRedditSource.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-reddit/expected/onlyRedditSource.yml")
    void onlyRedditSource() {
        foundOnReddit.run();
    }
}
