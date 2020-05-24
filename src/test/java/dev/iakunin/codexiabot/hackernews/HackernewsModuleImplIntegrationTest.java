package dev.iakunin.codexiabot.hackernews;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Request;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import java.util.stream.Stream;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HackernewsModuleImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private HackernewsModuleImpl hackernewsModule;

    @Test
    @DataSet(
        value = "db-rider/hackernews/hackernews-module/initial/noItemInDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/hackernews-module/expected/noItemInDatabase.yml")
    public void noItemInDatabase() {
        WireMockServer.stub(
            new Stub(
                new Request("/hackernews/item/4.json"),
                new Response(
                    new ResourceOf("wiremock/hackernews/hackernews-module/4.json")
                )
            )
        );

        hackernewsModule.healthCheckItems(Stream.of(4));
    }
}
