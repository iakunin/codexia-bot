package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class ForksUpIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ForksUp forksUp;

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/emptyDatabase.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noStarsUpResults_githubRepoWithoutStat.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noStarsUpResults_githubRepoWithoutStat.yml")
    public void noStarsUpResults_githubRepoWithoutStat() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noStarsUpResults_githubRepoWithOneStat.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noStarsUpResults_githubRepoWithOneStat.yml")
    public void noStarsUpResults_githubRepoWithOneStat() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noStarsUpResults_githubRepoWithTwoStats_withTwoEqualStars.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noStarsUpResults_githubRepoWithTwoStats_withTwoEqualStars.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_withTwoEqualStars() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noStarsUpResults_githubRepoWithTwoStats_starsDecrease.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noStarsUpResults_githubRepoWithTwoStats_starsDecrease.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_starsDecrease() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_starsIncreaseLessThan10() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml")
    public void noStarsUpResults_githubRepoWithThreeStats_starsIncreaseLessThan10() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void noStarsUpResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noStarsUpResults_githubRepoWithTwoStats_happyPath.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noStarsUpResults_githubRepoWithTwoStats_happyPath.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_happyPath() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/noStarsUpResults_githubRepoWithThreeStats_happyPath.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/noStarsUpResults_githubRepoWithThreeStats_happyPath.yml")
    public void noStarsUpResults_githubRepoWithThreeStats_happyPath() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneStarsUpResult_githubRepoWithThreeStats_happyPath.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneStarsUpResult_githubRepoWithThreeStats_happyPath.yml")
    public void oneStarsUpResult_githubRepoWithThreeStats_happyPath() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneStarsUpResult_githubRepoWithFourStats_happyPath.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneStarsUpResult_githubRepoWithFourStats_happyPath.yml")
    public void oneStarsUpResult_githubRepoWithFourStats_happyPath() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneStarsUpResult_noNewGithubStats.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneStarsUpResult_noNewGithubStats.yml")
    public void oneStarsUpResult_noNewGithubStats() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneStarsUpResult_starsDecrease.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneStarsUpResult_starsDecrease.yml")
    public void oneStarsUpResult_starsDecrease() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneStarsUpResult_starsIncreaseLessThan10.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneStarsUpResult_starsIncreaseLessThan10.yml")
    public void oneStarsUpResult_starsIncreaseLessThan10() {
        forksUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/forks-up/initial/oneStarsUpResult_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/forks-up/expected/oneStarsUpResult_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void oneStarsUpResult_starsIncreaseMoreThan10ButLessThan5Percents() {
        forksUp.run();
    }
}
