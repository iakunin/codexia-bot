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
public class SendReviewsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SendReviews runnable;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-reviews/initial/noReviewsToSend.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-reviews/expected/noReviewsToSend.yml")
    public void noReviewsToSend() {
        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-reviews/initial/reviewSuccessfullySent.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-reviews/expected/reviewSuccessfullySent.yml")
    public void reviewSuccessfullySent() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(
                    RequestMethod.POST,
                    WireMock.urlPathMatching("/codexia/p/\\d+/post")
                ),
                new Response()
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/codexia/cron/send-reviews/initial/reviewSentWithDuplicate_responseBodyExists.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/send-reviews/expected/reviewSentWithDuplicate_responseBodyExists.yml"
    )
    public void reviewSentWithDuplicateResponseBodyExists() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(
                    RequestMethod.POST,
                    WireMock.urlPathMatching("/codexia/p/\\d+/post")
                ),
                new Response(HttpStatus.NOT_FOUND.value(), "Review already exists")
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(value =
        "db-rider/codexia/cron/send-reviews/initial/reviewSentWithDuplicate_responseBodyEmpty.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet(
        "db-rider/codexia/cron/send-reviews/expected/reviewSentWithDuplicate_responseBodyEmpty.yml"
    )
    public void reviewSentWithDuplicateResponseBodyEmpty() {
        new WireMockWrapper().stub(
            new Stub(
                new Request(
                    RequestMethod.POST,
                    WireMock.urlPathMatching("/codexia/p/\\d+/post")
                ),
                new Response(HttpStatus.NOT_FOUND.value())
            )
        );

        this.runnable.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-reviews/initial/reviewSentWith500.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-reviews/expected/reviewSentWith500.yml")
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
