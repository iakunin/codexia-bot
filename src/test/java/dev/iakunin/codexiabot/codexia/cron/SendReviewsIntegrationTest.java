package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = SendReviewsIntegrationTest.Initializer.class)
public class SendReviewsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SendReviews sendReviews;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-reviews/initial/noReviewsToSend.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-reviews/expected/noReviewsToSend.yml")
    public void noReviewsToSend() {
        sendReviews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-reviews/initial/reviewSuccessfullySent.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-reviews/expected/reviewSuccessfullySent.yml")
    public void reviewSuccessfullySent() {
        WireMockServer.getInstance().stubFor(
            post(urlPathMatching("/p/\\d+/post"))
                .willReturn(ok())
        );

        sendReviews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-reviews/initial/reviewSentWithDuplicate_responseBodyExists.yml",
        cleanAfter = true, cleanBefore = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-reviews/expected/reviewSentWithDuplicate_responseBodyExists.yml")
    public void reviewSentWithDuplicate_responseBodyExists() {
        WireMockServer.getInstance().stubFor(
            post(urlPathMatching("/p/\\d+/post"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody("Review already exists")
                )
        );

        sendReviews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-reviews/initial/reviewSentWithDuplicate_responseBodyEmpty.yml",
        cleanAfter = true, cleanBefore = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-reviews/expected/reviewSentWithDuplicate_responseBodyEmpty.yml")
    public void reviewSentWithDuplicate_responseBodyEmpty() {
        WireMockServer.getInstance().stubFor(
            post(urlPathMatching("/p/\\d+/post"))
                .willReturn(aResponse()
                    .withStatus(404)
                )
        );

        sendReviews.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-reviews/initial/reviewSentWith500.yml",
        cleanAfter = true, cleanBefore = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-reviews/expected/reviewSentWith500.yml")
    public void reviewSentWith500() {
        WireMockServer.getInstance().stubFor(
            post(urlPathMatching("/p/\\d+/post"))
                .willReturn(aResponse()
                    .withStatus(500)
                )
        );

        sendReviews.run();
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
