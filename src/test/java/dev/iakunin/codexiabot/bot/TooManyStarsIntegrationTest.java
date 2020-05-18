package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TooManyStarsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TooManyStars tooManyStars;

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/emptyDatabase.yml")
    void emptyDatabase() {
        tooManyStars.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithoutStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/noResults_githubRepoWithoutStat.yml")
    void noResults_githubRepoWithoutStat() {
        tooManyStars.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithOneEmptyStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/noResults_githubRepoWithOneEmptyStat.yml")
    void noResults_githubRepoWithOneEmptyStat() {
        tooManyStars.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithOneStat_belowThreshold.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/noResults_githubRepoWithOneStat_belowThreshold.yml")
    void noResults_githubRepoWithOneStat_belowThreshold() {
        tooManyStars.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithTwoStats_belowThreshold.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/noResults_githubRepoWithTwoStats_belowThreshold.yml")
    void noResults_githubRepoWithTwoStats_belowThreshold() {
        tooManyStars.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithOneStat_aboveThreshold.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/noResults_githubRepoWithOneStat_aboveThreshold.yml")
    void noResults_githubRepoWithOneStat_aboveThreshold() {
        tooManyStars.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithTwoStats_aboveThreshold.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/noResults_githubRepoWithTwoStats_aboveThreshold.yml")
    void noResults_githubRepoWithTwoStats_aboveThreshold() {
        tooManyStars.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/oneResult_noNewGithubStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/oneResult_noNewGithubStats.yml")
    void oneResult_noNewGithubStats() {
        tooManyStars.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/oneResult_oneNewGithubStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/oneResult_oneNewGithubStat.yml")
    void oneResult_oneNewGithubStat() {
        tooManyStars.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/oneResult_twoNewGithubStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/oneResult_twoNewGithubStats.yml")
    void oneResult_twoNewGithubStats() {
        tooManyStars.run();
    }
}
