package dev.iakunin.codexiabot.codexia.cron.throwaway;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class InitializeBadgesIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private InitializeBadges cron;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/throwaway/initialize-badges/expected/emptyDatabase.yml")
    void emptyDatabase() {
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/oneTooManyStars.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/throwaway/initialize-badges/expected/oneTooManyStars.yml")
    void oneTooManyStars() {
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/twoTooManyStars.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/throwaway/initialize-badges/expected/twoTooManyStars.yml")
    void twoTooManyStars() {
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/oneTooSmall.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/throwaway/initialize-badges/expected/oneTooSmall.yml")
    void oneTooSmall() {
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/multipleTooSmall.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/throwaway/initialize-badges/expected/multipleTooSmall.yml")
    void multipleTooSmall() {
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/fromTooSmallToNotSmall.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/throwaway/initialize-badges/expected/fromTooSmallToNotSmall.yml")
    void fromTooSmallToNotSmall() {
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/oneNotTooSmall.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/throwaway/initialize-badges/expected/oneNotTooSmall.yml")
    void oneNotTooSmall() {
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/altogether.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/throwaway/initialize-badges/expected/altogether.yml")
    void altogether() {
        cron.run();
    }
}
