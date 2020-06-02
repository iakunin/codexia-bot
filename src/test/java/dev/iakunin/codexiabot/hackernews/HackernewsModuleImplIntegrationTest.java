package dev.iakunin.codexiabot.hackernews;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockWrapper;
import dev.iakunin.codexiabot.util.wiremock.Request;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import java.util.stream.Stream;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HackernewsModuleImplIntegrationTest extends AbstractIntegrationTest {

    private static final int EXTERNAL_ID = 4;

    @Autowired
    private HackernewsModuleImpl hackernews;

    @Test
    @DataSet(
        value = "db-rider/hackernews/hackernews-module/initial/noItemInDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/hackernews/hackernews-module/expected/noItemInDatabase.yml")
    public void noItemInDatabase() {
        new WireMockWrapper().stub(
            new Stub(
                new Request("/hackernews/item/4.json"),
                new Response(
                    new ResourceOf("wiremock/hackernews/hackernews-module/4.json")
                )
            )
        );

        this.hackernews.healthCheckItems(Stream.of(EXTERNAL_ID));
    }
}
