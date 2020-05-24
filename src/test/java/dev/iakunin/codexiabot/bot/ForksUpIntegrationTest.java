package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class ForksUpIntegrationTest extends AbstractIntegrationTest {

    @Qualifier("forksUp")
    @Autowired
    private Up forksUp;

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithoutStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithoutStat.yml")
    public void noResults_githubRepoWithoutStat() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithOneStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithOneStat.yml")
    public void noResults_githubRepoWithOneStat() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoEmptyStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoEmptyStats.yml")
    public void noResults_githubRepoWithTwoEmptyStats() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithOneEmptyAndOneNonEmptyStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithOneEmptyAndOneNonEmptyStat.yml")
    public void noResults_githubRepoWithOneEmptyAndOneNonEmptyStat() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_withTwoEqualForks.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_withTwoEqualForks.yml")
    public void noResults_githubRepoWithTwoStats_withTwoEqualForks() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_forksDecrease.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_forksDecrease.yml")
    public void noResults_githubRepoWithTwoStats_forksDecrease() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_forksIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_forksIncreaseLessThan10.yml")
    public void noResults_githubRepoWithTwoStats_forksIncreaseLessThan10() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithThreeStats_forksIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithThreeStats_forksIncreaseLessThan10.yml")
    public void noResults_githubRepoWithThreeStats_forksIncreaseLessThan10() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_forksIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_forksIncreaseMoreThan10ButLessThan5Percents.yml")
    public void noResults_githubRepoWithTwoStats_forksIncreaseMoreThan10ButLessThan5Percents() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithThreeStats_forksIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithThreeStats_forksIncreaseMoreThan10ButLessThan5Percents.yml")
    public void noResults_githubRepoWithThreeStats_forksIncreaseMoreThan10ButLessThan5Percents() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_happyPath.yml")
    public void noResults_githubRepoWithTwoStats_happyPath() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithThreeStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithThreeStats_happyPath.yml")
    public void noResults_githubRepoWithThreeStats_happyPath() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_githubRepoWithThreeStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_githubRepoWithThreeStats_happyPath.yml")
    public void oneResult_githubRepoWithThreeStats_happyPath() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_githubRepoWithFourStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_githubRepoWithFourStats_happyPath.yml")
    public void oneResult_githubRepoWithFourStats_happyPath() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_noNewGithubStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_noNewGithubStats.yml")
    public void oneResult_noNewGithubStats() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_forksDecrease.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_forksDecrease.yml")
    public void oneResult_forksDecrease() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_forksIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_forksIncreaseLessThan10.yml")
    public void oneResult_forksIncreaseLessThan10() {
        forksUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_forksIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_forksIncreaseMoreThan10ButLessThan5Percents.yml")
    public void oneResult_forksIncreaseMoreThan10ButLessThan5Percents() {
        forksUp.run();
    }
}
