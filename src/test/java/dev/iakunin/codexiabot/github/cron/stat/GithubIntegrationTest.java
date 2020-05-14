package dev.iakunin.codexiabot.github.cron.stat;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.CodexiaBotApplication;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import lombok.SneakyThrows;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = {
    AbstractIntegrationTest.TestConfig.class,
    GithubIntegrationTest.TestConfig.class,
})
public class GithubIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private Github github;

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/github/initial/emptyDatabase.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/github/expected/emptyDatabase.yml")
    public void emptyDatabase() {
        github.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/github/initial/notCodexiaSource.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/github/expected/notCodexiaSource.yml")
    public void notCodexiaSource() {
        github.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/github/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/github/expected/happyPath.yml")
    public void oneWithoutGithubRepo() {
        WireMockServer.stub(
            new Stub(
                "/repos/instaloader/instaloader",
                new ResourceOf("wiremock/github/cron/stat/github/instaloader.json")
            )
        );

        github.run();
    }

    @Test
    @DataSet(
        value = "db-rider/github/cron/stat/github/initial/githubIoException.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/cron/stat/github/expected/githubIoException.yml")
    public void githubIoException() {
        WireMockServer.stub(
            new Stub(
                "/repos/instaloader/instaloader",
                new Response(HttpStatus.INTERNAL_SERVER_ERROR.value())
            )
        );

        github.run();
    }

    @AfterEach
    void after() {
        WireMockServer.getInstance().resetAll();
    }

    @Configuration
    @Import(CodexiaBotApplication.class)
    static class TestConfig {
        @SneakyThrows
        @Bean
        public GitHub gitHub() {
            return new GitHubBuilder()
                .withEndpoint(
                    WireMockServer.getInstance().baseUrl()
                ).build();
        }
    }
}
