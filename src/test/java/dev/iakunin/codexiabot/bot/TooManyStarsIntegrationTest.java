package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TooManyStarsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TooManyStars runnable;

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithoutStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/noResults_githubRepoWithoutStat.yml")
    public void noResultsGithubRepoWithoutStat() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithOneEmptyStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/too-many-stars/expected/noResults_githubRepoWithOneEmptyStat.yml"
    )
    public void noResultsGithubRepoWithOneEmptyStat() {
        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithOneStat_belowThreshold.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/too-many-stars/expected/noResults_githubRepoWithOneStat_belowThreshold.yml"
    )
    public void noResultsGithubRepoWithOneStatBelowThreshold() {
        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithTwoStats_belowThreshold.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/too-many-stars/expected/noResults_githubRepoWithTwoStats_belowThreshold.yml"
    )
    public void noResultsGithubRepoWithTwoStatsBelowThreshold() {
        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithOneStat_aboveThreshold.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/too-many-stars/expected/noResults_githubRepoWithOneStat_aboveThreshold.yml"
    )
    public void noResultsGithubRepoWithOneStatAboveThreshold() {
        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/bot/too-many-stars/initial/noResults_githubRepoWithTwoStats_aboveThreshold.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/too-many-stars/expected/noResults_githubRepoWithTwoStats_aboveThreshold.yml"
    )
    public void noResultsGithubRepoWithTwoStatsAboveThreshold() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/oneResult_noNewGithubStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/oneResult_noNewGithubStats.yml")
    public void oneResultNoNewGithubStats() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/oneResult_oneNewGithubStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/oneResult_oneNewGithubStat.yml")
    public void oneResultOneNewGithubStat() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars/initial/oneResult_twoNewGithubStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars/expected/oneResult_twoNewGithubStats.yml")
    public void oneResultTwoNewGithubStats() {
        this.runnable.run();
    }
}
