package dev.iakunin.codexiabot.codexia.cron.throwaway;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InitializeBadgesIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private InitializeBadges cron;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/throwaway/initialize-badges/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        this.cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/oneTooManyStars.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/throwaway/initialize-badges/expected/oneTooManyStars.yml"
    )
    public void oneTooManyStars() {
        this.cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/twoTooManyStars.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/throwaway/initialize-badges/expected/twoTooManyStars.yml"
    )
    public void twoTooManyStars() {
        this.cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/oneTooSmall.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/throwaway/initialize-badges/expected/oneTooSmall.yml"
    )
    public void oneTooSmall() {
        this.cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/multipleTooSmall.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/throwaway/initialize-badges/expected/multipleTooSmall.yml"
    )
    public void multipleTooSmall() {
        this.cron.run();
    }

    @Test
    @DataSet(value =
        "db-rider/codexia/cron/throwaway/initialize-badges/initial/fromTooSmallToNotSmall.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/throwaway/initialize-badges/expected/fromTooSmallToNotSmall.yml"
    )
    public void fromTooSmallToNotSmall() {
        this.cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/oneNotTooSmall.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/throwaway/initialize-badges/expected/oneNotTooSmall.yml"
    )
    public void oneNotTooSmall() {
        this.cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/throwaway/initialize-badges/initial/altogether.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/throwaway/initialize-badges/expected/altogether.yml")
    public void altogether() {
        this.cron.run();
    }
}
