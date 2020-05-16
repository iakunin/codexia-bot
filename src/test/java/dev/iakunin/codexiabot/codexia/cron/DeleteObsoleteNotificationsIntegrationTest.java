package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteObsoleteNotificationsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private DeleteObsoleteNotifications deleteObsoleteNotifications;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/nothingToDeleteOneReview.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/nothingToDeleteOneReview.yml")
    public void nothingToDeleteOneReview() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/nothingToDeleteTwoReviews.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/nothingToDeleteTwoReviews.yml")
    public void nothingToDeleteTwoReviews() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/deletedOneNotificationPerOneReview.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/deletedOneNotificationPerOneReview.yml")
    public void deletedOneNotificationPerOneReview() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/deletedTwoNotificationsPerOneReview.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/deletedTwoNotificationsPerOneReview.yml")
    public void deletedTwoNotificationsPerOneReview() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/deletedOneNotificationPerTwoReviews.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/deletedOneNotificationPerTwoReviews.yml")
    public void deletedOneNotificationPerTwoReviews() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/deletedTwoNotificationsPerTwoReviews.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/deletedTwoNotificationsPerTwoReviews.yml")
    public void deletedTwoNotificationsPerTwoReviews() {
        deleteObsoleteNotifications.run();
    }
}
