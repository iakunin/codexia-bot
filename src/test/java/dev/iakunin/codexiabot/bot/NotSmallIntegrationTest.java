package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NotSmallIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private NotSmall notSmall;

    @Test
    @DataSet(
        value = "db-rider/bot/not-small/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/not-small/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        notSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/not-small/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/not-small/expected/happyPath.yml")
    public void happyPath() {
        notSmall.run();
    }

}
