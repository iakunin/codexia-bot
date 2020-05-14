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

@ContextConfiguration(initializers = ResendErroneousReviewsIntegrationTest.Initializer.class)
public class ResendErroneousReviewsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ResendErroneousReviews resendErroneousReviews;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-erroneous/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        resendErroneousReviews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/noErroneous.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-erroneous/expected/noErroneous.yml")
    public void noErroneous() {
        resendErroneousReviews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/oneErroneous.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-erroneous/expected/oneErroneous.yml")
    public void oneErroneous() {
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, WireMock.urlPathMatching("/p/\\d+/post")),
                new Response("Review successfully sent")
            )
        );

        resendErroneousReviews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/oneSuccessfulOneErroneous.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-erroneous/expected/oneSuccessfulOneErroneous.yml")
    public void oneSuccessfulOneErroneous() {
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, WireMock.urlPathMatching("/p/\\d+/post")),
                new Response("Review successfully sent")
            )
        );

        resendErroneousReviews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/twoErroneous.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-erroneous/expected/twoErroneous.yml")
    public void twoErroneous() {
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, WireMock.urlPathMatching("/p/\\d+/post")),
                new Response("Review successfully sent")
            )
        );

        resendErroneousReviews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/oneErroneous.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-erroneous/expected/reviewSentWith500.yml")
    public void reviewSentWith500() {
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, WireMock.urlPathMatching("/p/\\d+/post")),
                new Response(HttpStatus.INTERNAL_SERVER_ERROR.value())
            )
        );

        resendErroneousReviews.run();
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
