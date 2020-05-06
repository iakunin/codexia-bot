package dev.iakunin.codexiabot.github;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.CodexiaBotApplication;
import dev.iakunin.codexiabot.github.GithubModule.RepoNotFoundException;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import java.io.IOException;
import lombok.SneakyThrows;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.FormattedText;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = {
    AbstractIntegrationTest.TestConfig.class,
    GithubModuleCreateRepoIntegrationTest.TestConfig.class,
})
@ContextConfiguration(initializers = GithubModuleCreateRepoIntegrationTest.Initializer.class)
public class GithubModuleCreateRepoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private GithubModuleImpl module;

    @Test
    @DataSet(
        value = "db-rider/github/github-module/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/github-module/expected/happyPath.yml")
    public void happyPath() throws IOException {
        WireMockServer.stub(
            new Stub(
                "/github/repos/casbin/casbin-rs",
                new ResourceOf("wiremock/github/github-module/github/getRepo.json")
            )
        );

        module.createRepo(
            new GithubModule.CreateArguments()
                .setSource(GithubModule.Source.CODEXIA)
                .setExternalId("1662")
                .setUrl("https://github.com/casbin/casbin-rs")
        );
    }

    @Test
    @DataSet(
        value = "db-rider/github/github-module/initial/repoExistsByFullName.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/github-module/expected/repoExistsByFullName.yml")
    public void repoExistsByFullName() throws IOException {
        WireMockServer.stub(
            new Stub(
                "/github/repos/casbin/casbin-rs",
                new ResourceOf("wiremock/github/github-module/github/getRepo.json")
            )
        );

        module.createRepo(
            new GithubModule.CreateArguments()
                .setSource(GithubModule.Source.CODEXIA)
                .setExternalId("1662")
                .setUrl("https://github.com/casbin/casbin-rs")
        );
    }

    @Test
    @DataSet(
        value = "db-rider/github/github-module/initial/repoExistsByExternalId.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/github-module/expected/repoExistsByExternalId.yml")
    public void repoExistsByExternalId() throws IOException {
        WireMockServer.stub(
            new Stub(
                "/github/repos/casbin/casbin-rs",
                new ResourceOf("wiremock/github/github-module/github/getRepo.json")
            )
        );

        module.createRepo(
            new GithubModule.CreateArguments()
                .setSource(GithubModule.Source.CODEXIA)
                .setExternalId("1662")
                .setUrl("https://github.com/casbin/casbin-rs")
        );
    }

    @Test
    @DataSet(
        value = "db-rider/github/github-module/initial/repoExistsByCodexiaSource.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/github-module/expected/repoExistsByCodexiaSource.yml")
    public void repoExistsByCodexiaSource() throws IOException {
        WireMockServer.stub(
            new Stub(
                "/github/repos/casbin/casbin-rs",
                new ResourceOf("wiremock/github/github-module/github/getRepo.json")
            )
        );

        module.createRepo(
            new GithubModule.CreateArguments()
                .setSource(GithubModule.Source.CODEXIA)
                .setExternalId("1662")
                .setUrl("https://github.com/casbin/casbin-rs")
        );
    }

    @Test
    @DataSet(
        value = "db-rider/github/github-module/initial/notFoundInGithub.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/github-module/expected/notFoundInGithub.yml")
    public void notFoundInGithub() throws IOException {
        WireMockServer.stub(
            new Stub(
                "/github/repos/casbin/casbin-rs",
                new Response(
                    HttpStatus.NOT_FOUND.value(),
                    new ResourceOf("wiremock/github/github-module/github/repoNotFound.json")
                )
            )
        );

        final RepoNotFoundException exception = assertThrows(
            RepoNotFoundException.class,
            () -> module.createRepo(
                new GithubModule.CreateArguments()
                    .setSource(GithubModule.Source.CODEXIA)
                    .setExternalId("1662")
                    .setUrl("https://github.com/casbin/casbin-rs")
            )
        );
        assertEquals(
            exception.getMessage(),
            new FormattedText(
                "Unable to find github repo by url='%s'",
                "https://github.com/casbin/casbin-rs"
            ).asString()
        );
    }

    @AfterEach
    void after() {
        WireMockServer.getInstance().resetAll();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                "app.codexia.base-url=" + WireMockServer.getInstance().baseUrl() + "/codexia"
            ).applyTo(applicationContext.getEnvironment());
        }
    }

    @Configuration
    @Import(CodexiaBotApplication.class)
    static class TestConfig {
        @SneakyThrows
        @Bean
        public GitHub gitHub() {
            return new GitHubBuilder()
                .withEndpoint(
                    WireMockServer.getInstance().baseUrl() + "/github"
                ).build();
        }
    }
}