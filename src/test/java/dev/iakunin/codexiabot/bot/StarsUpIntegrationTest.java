package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class StarsUpIntegrationTest extends AbstractIntegrationTest {

    @Qualifier("starsUp")
    @Autowired
    private Up runnable;

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithoutStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithoutStat.yml")
    public void noResultsGithubRepoWithoutStat() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithOneStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithOneStat.yml")
    public void noResultsGithubRepoWithOneStat() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithTwoEmptyStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithTwoEmptyStats.yml")
    public void noResultsGithubRepoWithTwoEmptyStats() {
        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/bot/stars-up/initial/noResults_githubRepoWithOneEmptyAndOneNonEmptyStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected/noResults_githubRepoWithOneEmptyAndOneNonEmptyStat.yml"
    )
    public void noResultsGithubRepoWithOneEmptyAndOneNonEmptyStat() {
        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/bot/stars-up/initial/noResults_githubRepoWithTwoStats_withTwoEqualStars.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected/noResults_githubRepoWithTwoStats_withTwoEqualStars.yml"
    )
    public void noResultsGithubRepoWithTwoStatsWithTwoEqualStars() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithTwoStats_starsDecrease.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected/noResults_githubRepoWithTwoStats_starsDecrease.yml"
    )
    public void noResultsGithubRepoWithTwoStatsStarsDecrease() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial"
            + "/noResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected"
            + "/noResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml"
    )
    public void noResultsRepoWithTwoStatsIncreaseLessThanTen() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial"
            + "/noResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected"
            + "/noResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml"
    )
    public void noResultsRepoWithThreeStatsStarsIncreaseLessThanTen() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial"
            + "/noResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected"
            + "/noResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml"
    )
    public void noResultsRepoWithTwoStatsStarsIncreaseMoreThanTenButLessThanFivePercents() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial"
            + "/noResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected"
            + "/noResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml"
    )
    public void noResultsRepoWithThreeStatsStarsIncreaseMoreThanTenButLessThanFivePercents() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithTwoStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected/noResults_githubRepoWithTwoStats_happyPath.yml"
    )
    public void noResultsGithubRepoWithTwoStatsHappyPath() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithThreeStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected/noResults_githubRepoWithThreeStats_happyPath.yml"
    )
    public void noResultsGithubRepoWithThreeStatsHappyPath() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneResult_githubRepoWithThreeStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected/oneResult_githubRepoWithThreeStats_happyPath.yml"
    )
    public void oneResultGithubRepoWithThreeStatsHappyPath() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneResult_githubRepoWithFourStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected/oneResult_githubRepoWithFourStats_happyPath.yml"
    )
    public void oneResultGithubRepoWithFourStatsHappyPath() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneResult_noNewGithubStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneResult_noNewGithubStats.yml")
    public void oneResultNoNewGithubStats() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneResult_starsDecrease.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneResult_starsDecrease.yml")
    public void oneResultStarsDecrease() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneResult_starsIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneResult_starsIncreaseLessThan10.yml")
    public void oneResultStarsIncreaseLessThanTen() {
        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/bot/stars-up/initial/oneResult_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/stars-up/expected/oneResult_starsIncreaseMoreThan10ButLessThan5Percents.yml"
    )
    public void oneResultStarsIncreaseMoreThanTenButLessThanFivePercents() {
        this.runnable.run();
    }
}
