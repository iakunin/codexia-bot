package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ForksUpIntegrationTest extends AbstractIntegrationTest {

    @Qualifier("forksUp")
    @Autowired
    private Up runnable;

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithoutStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithoutStat.yml")
    public void noResultsGithubRepoWithoutStat() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithOneStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithOneStat.yml")
    public void noResultsGithubRepoWithOneStat() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoEmptyStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoEmptyStats.yml")
    public void noResultsGithubRepoWithTwoEmptyStats() {
        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/bot/forks-up/initial/noResults_githubRepoWithOneEmptyAndOneNonEmptyStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected/noResults_githubRepoWithOneEmptyAndOneNonEmptyStat.yml"
    )
    public void noResultsGithubRepoWithOneEmptyAndOneNonEmptyStat() {
        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_withTwoEqualForks.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_withTwoEqualForks.yml"
    )
    public void noResultsGithubRepoWithTwoStatsWithTwoEqualForks() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_forksDecrease.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_forksDecrease.yml"
    )
    public void noResultsGithubRepoWithTwoStatsForksDecrease() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial"
            + "/noResults_githubRepoWithTwoStats_forksIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected"
            + "/noResults_githubRepoWithTwoStats_forksIncreaseLessThan10.yml"
    )
    public void noResultsGithubRepoWithTwoStatsForksIncreaseLessThanTen() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial"
            + "/noResults_githubRepoWithThreeStats_forksIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected"
            + "/noResults_githubRepoWithThreeStats_forksIncreaseLessThan10.yml"
    )
    public void noResultsGithubRepoWithThreeStatsForksIncreaseLessThanTen() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial"
            + "/noResults_githubRepoWithTwoStats_forksIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected"
            + "/noResults_githubRepoWithTwoStats_forksIncreaseMoreThan10ButLessThan5Percents.yml"
    )
    public void noResultsRepoWithTwoStatsForksIncreaseMoreThanTenButLessThanFivePercents() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial"
            + "/noResults_githubRepoWithThreeStats_forksIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected"
            + "/noResults_githubRepoWithThreeStats_forksIncreaseMoreThan10ButLessThan5Percents.yml"
    )
    public void noResultsRepoWithThreeStatsForksIncreaseMoreThanTenButLessThanFivePercents() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_happyPath.yml"
    )
    public void noResultsGithubRepoWithTwoStatsHappyPath() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithThreeStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected/noResults_githubRepoWithThreeStats_happyPath.yml"
    )
    public void noResultsGithubRepoWithThreeStatsHappyPath() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_githubRepoWithThreeStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected/oneResult_githubRepoWithThreeStats_happyPath.yml"
    )
    public void oneResultGithubRepoWithThreeStatsHappyPath() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_githubRepoWithFourStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected/oneResult_githubRepoWithFourStats_happyPath.yml"
    )
    public void oneResultGithubRepoWithFourStatsHappyPath() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_noNewGithubStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_noNewGithubStats.yml")
    public void oneResultNoNewGithubStats() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_forksDecrease.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_forksDecrease.yml")
    public void oneResultForksDecrease() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_forksIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_forksIncreaseLessThan10.yml")
    public void oneResultForksIncreaseLessThanTen() {
        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/bot/forks-up/initial/oneResult_forksIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/bot/forks-up/expected/oneResult_forksIncreaseMoreThan10ButLessThan5Percents.yml"
    )
    public void oneResultForksIncreaseMoreThanTenButLessThanFivePercents() {
        this.runnable.run();
    }
}
