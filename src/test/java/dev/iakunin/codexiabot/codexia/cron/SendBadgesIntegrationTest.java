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

/**
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class SendBadgesIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SendBadges cron;

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-badges/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-badges/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        this.cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-badges/initial/attachOneBadge.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-badges/expected/attachOneBadge.yml")
    public void attachOneBadge() {
        final var pattern = WireMock.urlPathEqualTo("/codexia/p/123/attach");
        new WireMockWrapper().stub(
            new Stub(
                new Request(RequestMethod.POST, pattern),
                new Response("Badge successfully attached")
            )
        );

        this.cron.run();

        new WireMockWrapper().verify(1, WireMock.postRequestedFor(pattern));
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-badges/initial/attachTwoBadges.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-badges/expected/attachTwoBadges.yml")
    public void attachTwoBadges() {
        final var first = WireMock.urlPathEqualTo("/codexia/p/123/attach");
        new WireMockWrapper().stub(
            new Stub(
                new Request(RequestMethod.POST, first),
                new Response("Badge successfully attached")
            )
        );
        final var second = WireMock.urlPathEqualTo("/codexia/p/321/attach");
        new WireMockWrapper().stub(
            new Stub(
                new Request(RequestMethod.POST, second),
                new Response("Badge successfully attached")
            )
        );

        this.cron.run();

        new WireMockWrapper().verify(1, WireMock.postRequestedFor(first));
        new WireMockWrapper().verify(1, WireMock.postRequestedFor(second));
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-badges/initial/detachOneBadge.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-badges/expected/detachOneBadge.yml")
    public void detachOneBadge() {
        final var pattern = WireMock.urlPathEqualTo("/codexia/p/333/detach/bad");
        new WireMockWrapper().stub(
            new Stub(
                new Request(RequestMethod.POST, pattern),
                new Response("Badge successfully detached")
            )
        );

        this.cron.run();

        new WireMockWrapper().verify(1, WireMock.postRequestedFor(pattern));
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-badges/initial/detachTwoBadges.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-badges/expected/detachTwoBadges.yml")
    public void detachTwoBadges() {
        final var first = WireMock.urlPathEqualTo("/codexia/p/123/detach/bad");
        new WireMockWrapper().stub(
            new Stub(
                new Request(RequestMethod.POST, first),
                new Response("Badge successfully detached")
            )
        );
        final var second = WireMock.urlPathEqualTo("/codexia/p/321/detach/bad");
        new WireMockWrapper().stub(
            new Stub(
                new Request(RequestMethod.POST, second),
                new Response("Badge successfully detached")
            )
        );

        this.cron.run();

        new WireMockWrapper().verify(1, WireMock.postRequestedFor(second));
        new WireMockWrapper().verify(1, WireMock.postRequestedFor(first));
    }
}
