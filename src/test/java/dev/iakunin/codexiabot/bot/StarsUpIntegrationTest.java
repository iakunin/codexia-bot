package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

public class StarsUpIntegrationTest extends AbstractIntegrationTest {

    @Qualifier("starsUp")
    @Autowired
    private Up starsUp;

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/emptyDatabase.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noStarsUpResults_githubRepoWithoutStat.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noStarsUpResults_githubRepoWithoutStat.yml")
    public void noStarsUpResults_githubRepoWithoutStat() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noStarsUpResults_githubRepoWithOneStat.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noStarsUpResults_githubRepoWithOneStat.yml")
    public void noStarsUpResults_githubRepoWithOneStat() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noStarsUpResults_githubRepoWithTwoStats_withTwoEqualStars.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noStarsUpResults_githubRepoWithTwoStats_withTwoEqualStars.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_withTwoEqualStars() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noStarsUpResults_githubRepoWithTwoStats_starsDecrease.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noStarsUpResults_githubRepoWithTwoStats_starsDecrease.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_starsDecrease() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseLessThan10.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_starsIncreaseLessThan10() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseLessThan10.yml")
    public void noStarsUpResults_githubRepoWithThreeStats_starsIncreaseLessThan10() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noStarsUpResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_starsIncreaseMoreThan10ButLessThan5Percents() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noStarsUpResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void noStarsUpResults_githubRepoWithThreeStats_starsIncreaseMoreThan10ButLessThan5Percents() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noStarsUpResults_githubRepoWithTwoStats_happyPath.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noStarsUpResults_githubRepoWithTwoStats_happyPath.yml")
    public void noStarsUpResults_githubRepoWithTwoStats_happyPath() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/noStarsUpResults_githubRepoWithThreeStats_happyPath.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/noStarsUpResults_githubRepoWithThreeStats_happyPath.yml")
    public void noStarsUpResults_githubRepoWithThreeStats_happyPath() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneStarsUpResult_githubRepoWithThreeStats_happyPath.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneStarsUpResult_githubRepoWithThreeStats_happyPath.yml")
    public void oneStarsUpResult_githubRepoWithThreeStats_happyPath() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneStarsUpResult_githubRepoWithFourStats_happyPath.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneStarsUpResult_githubRepoWithFourStats_happyPath.yml")
    public void oneStarsUpResult_githubRepoWithFourStats_happyPath() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneStarsUpResult_noNewGithubStats.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneStarsUpResult_noNewGithubStats.yml")
    public void oneStarsUpResult_noNewGithubStats() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneStarsUpResult_starsDecrease.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneStarsUpResult_starsDecrease.yml")
    public void oneStarsUpResult_starsDecrease() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneStarsUpResult_starsIncreaseLessThan10.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneStarsUpResult_starsIncreaseLessThan10.yml")
    public void oneStarsUpResult_starsIncreaseLessThan10() {
        starsUp.run();
    }

    @Test
    @Transactional
    @DataSet(
        value = "db-rider/bot/stars-up/initial/oneStarsUpResult_starsIncreaseMoreThan10ButLessThan5Percents.yml",
        executeScriptsBefore = "db-rider/cleanup.sql", executeScriptsAfter = "db-rider/cleanup.sql",
        strategy = SeedStrategy.INSERT
    )
    @ExpectedDataSet("db-rider/bot/stars-up/expected/oneStarsUpResult_starsIncreaseMoreThan10ButLessThan5Percents.yml")
    public void oneStarsUpResult_starsIncreaseMoreThan10ButLessThan5Percents() {
        starsUp.run();
    }
}
