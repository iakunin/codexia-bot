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
    void emptyDatabase() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/happyPath.yml")
    void happyPath() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/moreThanThreshold.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/moreThanThreshold.yml")
    void moreThanThreshold() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/notInCodexia.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/notInCodexia.yml")
    void notInCodexia() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/oneSetResult.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/oneSetResult.yml")
    void oneSetResult() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/oneResetResult.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/oneResetResult.yml")
    void oneResetResult() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/linesOfCodeIsAbsent.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/linesOfCodeIsAbsent.yml")
    void linesOfCodeIsAbsent() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/githubStatIsAbsent.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/githubStatIsAbsent.yml")
    void githubStatIsAbsent() {
        tooSmall.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-small/initial/githubLanguageIsNull.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-small/expected/githubLanguageIsNull.yml")
    void githubLanguageIsNull() {
        tooSmall.run();
    }

}
