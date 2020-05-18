package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class StarsUpIntegrationTest extends AbstractIntegrationTest {

    @Qualifier("starsUp")
    @Autowired
    private Up starsUp;

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/emptyDatabase.yml")
    void emptyDatabase() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithoutStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithoutStat.yml")
    void noResults_githubRepoWithoutStat() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithOneStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithOneStat.yml")
    void noResults_githubRepoWithOneStat() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithTwoEmptyStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithTwoEmptyStats.yml")
    void noResults_githubRepoWithTwoEmptyStats() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithOneEmptyAndOneNonEmptyStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithOneEmptyAndOneNonEmptyStat.yml")
    void noResults_githubRepoWithOneEmptyAndOneNonEmptyStat() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithTwoStats_withTwoEqualStars.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithTwoStats_withTwoEqualStars.yml")
    void noResults_githubRepoWithTwoStats_withTwoEqualStars() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithTwoStats_starsDecrease.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithTwoStats_starsDecrease.yml")
    void noResults_githubRepoWithTwoStats_starsDecrease() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml")
    void noResults_githubRepoWithTwoStats_starsIncreaseLessThan10() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml")
    void noResults_githubRepoWithThreeStats_starsIncreaseLessThan10() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    void noResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    void noResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithTwoStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithTwoStats_happyPath.yml")
    void noResults_githubRepoWithTwoStats_happyPath() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noResults_githubRepoWithThreeStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noResults_githubRepoWithThreeStats_happyPath.yml")
    void noResults_githubRepoWithThreeStats_happyPath() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneResult_githubRepoWithThreeStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneResult_githubRepoWithThreeStats_happyPath.yml")
    void oneResult_githubRepoWithThreeStats_happyPath() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneResult_githubRepoWithFourStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneResult_githubRepoWithFourStats_happyPath.yml")
    void oneResult_githubRepoWithFourStats_happyPath() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneResult_noNewGithubStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneResult_noNewGithubStats.yml")
    void oneResult_noNewGithubStats() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneResult_starsDecrease.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneResult_starsDecrease.yml")
    void oneResult_starsDecrease() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneResult_starsIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneResult_starsIncreaseLessThan10.yml")
    void oneResult_starsIncreaseLessThan10() {
        starsUp.run();
    }

    @Test
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneResult_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneResult_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    void oneResult_starsIncreaseMoreThan10ButLessThan5Percents() {
        starsUp.run();
    }
}
