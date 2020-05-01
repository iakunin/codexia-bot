package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NotSmallAnymoreIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private NotSmallAnymore notSmallAnymore;

    @Test
    @DataSet(
        value = "db-rider/bot/not-small-anymore/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/not-small-anymore/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        notSmallAnymore.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/not-small-anymore/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/not-small-anymore/expected/happyPath.yml")
    public void happyPath() {
        notSmallAnymore.run();
    }

}
