package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Request;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = ResendReviewsUntilDuplicatedIntegrationTest.Initializer.class)
public class ResendReviewsUntilDuplicatedIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ResendReviewsUntilDuplicated cron;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-reviews-until-duplicated/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-reviews-until-duplicated/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-reviews-until-duplicated/initial/oneUnsuccessfulNotification.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-reviews-until-duplicated/expected/oneUnsuccessfulNotification.yml")
    public void oneUnsuccessfulNotification() {
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-reviews-until-duplicated/initial/oneSuccessfulNotificationWithDuplicatedCode.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-reviews-until-duplicated/expected/oneSuccessfulNotificationWithDuplicatedCode.yml")
    public void oneSuccessfulNotificationWithDuplicatedCode() {
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-reviews-until-duplicated/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-reviews-until-duplicated/expected/happyPath.yml")
    public void happyPath() {
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, WireMock.urlPathMatching("/p/\\d+/post")),
                new Response(HttpStatus.NOT_FOUND.value(), "Review already exists.")
            )
        );

        cron.run();
    }

    @AfterEach
    void after() {
        WireMockServer.getInstance().resetAll();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                "app.codexia.base-url=" + WireMockServer.getInstance().baseUrl()
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
