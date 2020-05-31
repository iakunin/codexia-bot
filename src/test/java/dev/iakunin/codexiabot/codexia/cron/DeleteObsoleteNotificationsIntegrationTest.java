package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class DeleteObsoleteNotificationsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private DeleteObsoleteNotifications runnable;

    @Autowired
    private CodexiaReviewRepository review;

    @Autowired
    private CodexiaReviewNotificationRepository notification;

    @Autowired
    private PlatformTransactionManager manager;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/delete-obsolete-notifications/expected/emptyDatabase.yml"
    )
    public void emptyDatabase() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/unableToDelete.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/delete-obsolete-notifications/expected/unableToDelete.yml"
    )
    public void unableToDelete() {
        new TransactionTemplate(this.manager).executeWithoutResult(
            status -> {
                final var repo = Mockito.mock(CodexiaReviewNotificationRepository.class);
                Mockito.when(repo.findAllByCodexiaReviewOrderByIdDesc(Mockito.any()))
                    .thenAnswer(
                        invocation -> this.notification.findAllByCodexiaReviewOrderByIdDesc(
                            invocation.getArgument(0)
                        )
                    );
                Mockito.doThrow(new RuntimeException())
                    .when(repo)
                    .delete(Mockito.any());

                new DeleteObsoleteNotifications(
                    this.review,
                    repo,
                    new DeleteObsoleteNotifications.Runner(repo)
                ).run();
            }
        );
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/initial/nothingToDeleteOneReview.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/expected/nothingToDeleteOneReview.yml"
    )
    public void nothingToDeleteOneReview() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/initial/nothingToDeleteTwoReviews.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/expected/nothingToDeleteTwoReviews.yml"
    )
    public void nothingToDeleteTwoReviews() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/initial/deletedOneNotificationPerOneReview.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/expected/deletedOneNotificationPerOneReview.yml"
    )
    public void deletedOneNotificationPerOneReview() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/initial/deletedTwoNotificationsPerOneReview.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/expected/deletedTwoNotificationsPerOneReview.yml"
    )
    public void deletedTwoNotificationsPerOneReview() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/initial/deletedOneNotificationPerTwoReviews.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/expected/deletedOneNotificationPerTwoReviews.yml"
    )
    public void deletedOneNotificationPerTwoReviews() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/initial/deletedTwoNotificationsPerTwoReviews.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/delete-obsolete-notifications"
            + "/expected/deletedTwoNotificationsPerTwoReviews.yml"
    )
    public void deletedTwoNotificationsPerTwoReviews() {
        this.runnable.run();
    }
}
