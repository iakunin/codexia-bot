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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        cron.run();
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-badges/initial/attachOneBadge.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-badges/expected/attachOneBadge.yml")
    public void attachOneBadge() {
        final var urlPattern = WireMock.urlPathEqualTo("/codexia/p/123/attach");
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, urlPattern),
                new Response("Badge successfully attached")
            )
        );

        cron.run();

        WireMockServer.getInstance().verify(1, WireMock.postRequestedFor(urlPattern));
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-badges/initial/attachTwoBadges.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-badges/expected/attachTwoBadges.yml")
    public void attachTwoBadges() {
        final var firstUrlPattern = WireMock.urlPathEqualTo("/codexia/p/123/attach");
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, firstUrlPattern),
                new Response("Badge successfully attached")
            )
        );
        final var secondUrlPattern = WireMock.urlPathEqualTo("/codexia/p/321/attach");
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, secondUrlPattern),
                new Response("Badge successfully attached")
            )
        );

        cron.run();

        WireMockServer.getInstance().verify(1, WireMock.postRequestedFor(firstUrlPattern));
        WireMockServer.getInstance().verify(1, WireMock.postRequestedFor(secondUrlPattern));
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-badges/initial/detachOneBadge.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-badges/expected/detachOneBadge.yml")
    public void detachOneBadge() {
        final var urlPattern = WireMock.urlPathEqualTo("/codexia/p/333/detach/bad");
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, urlPattern),
                new Response("Badge successfully detached")
            )
        );

        cron.run();

        WireMockServer.getInstance().verify(1, WireMock.postRequestedFor(urlPattern));
    }

    @Test
    @DataSet(
        value = "db-rider/codexia/cron/send-badges/initial/detachTwoBadges.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/codexia/cron/send-badges/expected/detachTwoBadges.yml")
    public void detachTwoBadges() {
        final var firstUrlPattern = WireMock.urlPathEqualTo("/codexia/p/123/detach/bad");
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, firstUrlPattern),
                new Response("Badge successfully detached")
            )
        );
        final var secondUrlPattern = WireMock.urlPathEqualTo("/codexia/p/321/detach/bad");
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, secondUrlPattern),
                new Response("Badge successfully detached")
            )
        );

        cron.run();

        WireMockServer.getInstance().verify(1, WireMock.postRequestedFor(secondUrlPattern));
        WireMockServer.getInstance().verify(1, WireMock.postRequestedFor(firstUrlPattern));
    }
}
