package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class TooSmallIntegrationTest extends AbstractIntegrationTest {

    @Qualifier("tooSmall")
    @Autowired
    private Small tooSmall;

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/happyPath.yml")
    public void happyPath() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/moreThanThreshold.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/moreThanThreshold.yml")
    public void moreThanThreshold() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/notInCodexia.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/notInCodexia.yml")
    public void notInCodexia() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/oneSetResult.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/oneSetResult.yml")
    public void oneSetResult() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/oneResetResult.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/oneResetResult.yml")
    public void oneResetResult() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/linesOfCodeIsAbsent.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/linesOfCodeIsAbsent.yml")
    public void linesOfCodeIsAbsent() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/githubStatIsAbsent.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/githubStatIsAbsent.yml")
    public void githubStatIsAbsent() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/githubLanguageIsNull.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/githubLanguageIsNull.yml")
    public void githubLanguageIsNull() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/projectLevelMoreThanZero.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/projectLevelMoreThanZero.yml")
    public void projectLevelMoreThanZero() {
        tooSmall.run();
    }

}
