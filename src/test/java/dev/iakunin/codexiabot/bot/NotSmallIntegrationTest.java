package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class NotSmallIntegrationTest extends AbstractIntegrationTest {

    @Qualifier("notSmall")
    @Autowired
    private Small notSmall;

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

    @Test
    @DataSet(
        value = "db-rider/bot/not-small/initial/noResult.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/not-small/expected/noResult.yml")
    public void noResult() {
        notSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/not-small/initial/linesOfCodeLessThanThreshold.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/not-small/expected/linesOfCodeLessThanThreshold.yml")
    public void linesOfCodeLessThanThreshold() {
        notSmall.run();
    }

}
