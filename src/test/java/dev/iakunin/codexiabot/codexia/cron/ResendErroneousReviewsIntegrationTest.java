package dev.iakunin.codexiabot.codexia.cron;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockWrapper;
import dev.iakunin.codexiabot.util.wiremock.Request;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ResendErroneousReviewsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ResendErroneousReviews runnable;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-erroneous/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/noErroneous.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-erroneous/expected/noErroneous.yml")
    public void noErroneous() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/oneErroneous.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-erroneous/expected/oneErroneous.yml")
    public void oneErroneous() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(
                    RequestMethod.POST,
                    WireMock.urlPathMatching("/codexia/p/\\d+/post")
                ),
                new Response("Review successfully sent")
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/oneSuccessfulOneErroneous.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/resend-erroneous/expected/oneSuccessfulOneErroneous.yml"
    )
    public void oneSuccessfulOneErroneous() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(
                    RequestMethod.POST,
                    WireMock.urlPathMatching("/codexia/p/\\d+/post")
                ),
                new Response("Review successfully sent")
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/twoErroneous.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-erroneous/expected/twoErroneous.yml")
    public void twoErroneous() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(
                    RequestMethod.POST,
                    WireMock.urlPathMatching("/codexia/p/\\d+/post")
                ),
                new Response("Review successfully sent")
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/resend-erroneous/initial/oneErroneous.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/resend-erroneous/expected/reviewSentWith500.yml")
    public void reviewSentWithInternalServerError() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(
                    RequestMethod.POST,
                    WireMock.urlPathMatching("/codexia/p/\\d+/post")
                ),
                new Response(HttpStatus.INTERNAL_SERVER_ERROR.value())
            )
        );

        this.runnable.run();
    }
}
