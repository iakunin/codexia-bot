package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class StarsUpIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private StarsUp starsUp;

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/emptyDatabase.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/noStarsUpResults_githubRepoWithoutStat.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/noStarsUpResults_githubRepoWithoutStat.yml")
    public void noStarsUpResults_githubRepoWithoutStat() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/noStarsUpResults_githubRepoWithOneStat.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/noStarsUpResults_githubRepoWithOneStat.yml")
    public void noStarsUpResults_githubRepoWithOneStat() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/noStarsUpResults_githubRepoWithTwoStats_withTwoEqualStars.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/noStarsUpResults_githubRepoWithTwoStats_withTwoEqualStars.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_withTwoEqualStars() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/noStarsUpResults_githubRepoWithTwoStats_starsDecrease.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/noStarsUpResults_githubRepoWithTwoStats_starsDecrease.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_starsDecrease() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_starsIncreaseLessThan10() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml")
    public void noStarsUpResults_githubRepoWithThreeStats_starsIncreaseLessThan10() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void noStarsUpResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/noStarsUpResults_githubRepoWithTwoStats_happyPath.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/noStarsUpResults_githubRepoWithTwoStats_happyPath.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_happyPath() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/noStarsUpResults_githubRepoWithThreeStats_happyPath.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/noStarsUpResults_githubRepoWithThreeStats_happyPath.yml")
    public void noStarsUpResults_githubRepoWithThreeStats_happyPath() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/oneStarsUpResult_githubRepoWithThreeStats_happyPath.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/oneStarsUpResult_githubRepoWithThreeStats_happyPath.yml")
    public void oneStarsUpResult_githubRepoWithThreeStats_happyPath() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/oneStarsUpResult_githubRepoWithFourStats_happyPath.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/oneStarsUpResult_githubRepoWithFourStats_happyPath.yml")
    public void oneStarsUpResult_githubRepoWithFourStats_happyPath() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/oneStarsUpResult_noNewGithubStats.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/oneStarsUpResult_noNewGithubStats.yml")
    public void oneStarsUpResult_noNewGithubStats() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/oneStarsUpResult_starsDecrease.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/oneStarsUpResult_starsDecrease.yml")
    public void oneStarsUpResult_starsDecrease() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/oneStarsUpResult_starsIncreaseLessThan10.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/oneStarsUpResult_starsIncreaseLessThan10.yml")
    public void oneStarsUpResult_starsIncreaseLessThan10() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/initial/oneStarsUpResult_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        cleanAfter = true, cleanBefore = true, disableConstraints = true
    )
    @ExpectedDataSet("db-rider/bot/expected/oneStarsUpResult_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void oneStarsUpResult_starsIncreaseMoreThan10ButLessThan5Percents() {
        starsUp.run();
    }
}
