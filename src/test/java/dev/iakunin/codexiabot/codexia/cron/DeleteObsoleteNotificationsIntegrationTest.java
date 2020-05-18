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

class DeleteObsoleteNotificationsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private DeleteObsoleteNotifications deleteObsoleteNotifications;

    @Autowired
    private CodexiaReviewRepository reviewRepository;

    @Autowired
    private CodexiaReviewNotificationRepository notificationRepository;

    @Autowired
    private DeleteObsoleteNotifications.Runner runner;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/emptyDatabase.yml")
    void emptyDatabase() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/unableToDelete.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/unableToDelete.yml")
    void unableToDelete() {
        new TransactionTemplate(transactionManager).executeWithoutResult(
            status -> {
                final CodexiaReviewNotificationRepository repoMock = Mockito.mock(CodexiaReviewNotificationRepository.class);
                Mockito.when(repoMock.findAllByCodexiaReviewOrderByIdDesc(Mockito.any()))
                    .thenAnswer(
                        invocation -> notificationRepository.findAllByCodexiaReviewOrderByIdDesc(
                            invocation.getArgument(0)
                        )
                    );
                Mockito.doThrow(new RuntimeException())
                    .when(repoMock)
                    .delete(Mockito.any());

                new DeleteObsoleteNotifications(
                    reviewRepository,
                    repoMock,
                    new DeleteObsoleteNotifications.Runner(repoMock)
                ).run();
            }
        );
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/nothingToDeleteOneReview.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/nothingToDeleteOneReview.yml")
    void nothingToDeleteOneReview() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/nothingToDeleteTwoReviews.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/nothingToDeleteTwoReviews.yml")
    void nothingToDeleteTwoReviews() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/deletedOneNotificationPerOneReview.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/deletedOneNotificationPerOneReview.yml")
    void deletedOneNotificationPerOneReview() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/deletedTwoNotificationsPerOneReview.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/deletedTwoNotificationsPerOneReview.yml")
    void deletedTwoNotificationsPerOneReview() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/deletedOneNotificationPerTwoReviews.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/deletedOneNotificationPerTwoReviews.yml")
    void deletedOneNotificationPerTwoReviews() {
        deleteObsoleteNotifications.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/delete-obsolete-notifications/initial/deletedTwoNotificationsPerTwoReviews.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/delete-obsolete-notifications/expected/deletedTwoNotificationsPerTwoReviews.yml")
    void deletedTwoNotificationsPerTwoReviews() {
        deleteObsoleteNotifications.run();
    }
}
