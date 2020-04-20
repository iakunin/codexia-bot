package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

public class ForksUpIntegrationTest extends AbstractIntegrationTest {

    @Qualifier("forksUp")
    @Autowired
    private Up forksUp;

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithoutStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithoutStat.yml")
    public void noResults_githubRepoWithoutStat() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithOneStat.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithOneStat.yml")
    public void noResults_githubRepoWithOneStat() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_withTwoEqualStars.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_withTwoEqualStars.yml")
    public void noResults_githubRepoWithTwoStats_withTwoEqualStars() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_starsDecrease.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_starsDecrease.yml")
    public void noResults_githubRepoWithTwoStats_starsDecrease() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml")
    public void noResults_githubRepoWithTwoStats_starsIncreaseLessThan10() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml")
    public void noResults_githubRepoWithThreeStats_starsIncreaseLessThan10() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void noResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void noResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithTwoStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithTwoStats_happyPath.yml")
    public void noResults_githubRepoWithTwoStats_happyPath() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noResults_githubRepoWithThreeStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noResults_githubRepoWithThreeStats_happyPath.yml")
    public void noResults_githubRepoWithThreeStats_happyPath() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_githubRepoWithThreeStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_githubRepoWithThreeStats_happyPath.yml")
    public void oneStarsUpResult_githubRepoWithThreeStats_happyPath() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_githubRepoWithFourStats_happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_githubRepoWithFourStats_happyPath.yml")
    public void oneStarsUpResult_githubRepoWithFourStats_happyPath() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_noNewGithubStats.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_noNewGithubStats.yml")
    public void oneStarsUpResult_noNewGithubStats() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_starsDecrease.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_starsDecrease.yml")
    public void oneStarsUpResult_starsDecrease() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_starsIncreaseLessThan10.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_starsIncreaseLessThan10.yml")
    public void oneStarsUpResult_starsIncreaseLessThan10() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneResult_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneResult_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void oneStarsUpResult_starsIncreaseMoreThan10ButLessThan5Percents() {
        forksUp.run();
    }
}
