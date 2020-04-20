package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FoundOnHackernewsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private FoundOnHackernews foundOnHackernews;

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-hackernews/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-hackernews/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        foundOnHackernews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-hackernews/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-hackernews/expected/happyPath.yml")
    public void happyPath() {
        foundOnHackernews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-hackernews/initial/reviewExists.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-hackernews/expected/reviewExists.yml")
    public void reviewExists() {
        foundOnHackernews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-hackernews/initial/onlyCodexiaSource.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-hackernews/expected/onlyCodexiaSource.yml")
    public void onlyCodexiaSource() {
        foundOnHackernews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/found-on-hackernews/initial/onlyHackernewsSource.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/found-on-hackernews/expected/onlyHackernewsSource.yml")
    public void onlyHackernewsSource() {
        foundOnHackernews.run();
    }
}
