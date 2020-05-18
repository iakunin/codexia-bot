package dev.iakunin.codexiabot.github;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.config.GithubConfig;
import dev.iakunin.codexiabot.github.GithubModule.RepoNotFoundException;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import java.io.IOException;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.FormattedText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = {
    AbstractIntegrationTest.TestConfig.class,
    GithubConfig.class,
})
class GithubModuleCreateRepoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private GithubModuleImpl module;

    @Test
    @DataSet(
        value = "db-rider/github/github-module/initial/happyPath.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/github-module/expected/happyPath.yml")
    void happyPath() throws IOException {
        WireMockServer.stub(
            new Stub(
                "/github/repos/casbin/casbin-rs",
                new ResourceOf("wiremock/github/github-module/github/getRepo.json")
            )
        );

        module.createRepo(
            new GithubModule.CreateArguments(
                "https://github.com/casbin/casbin-rs",
                GithubModule.Source.CODEXIA,
                "1662"
            )
        );
    }

    @Test
    @DataSet(
        value = "db-rider/github/github-module/initial/repoExistsByFullName.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/github-module/expected/repoExistsByFullName.yml")
    void repoExistsByFullName() throws IOException {
        WireMockServer.stub(
            new Stub(
                "/github/repos/casbin/casbin-rs",
                new ResourceOf("wiremock/github/github-module/github/getRepo.json")
            )
        );

        module.createRepo(
            new GithubModule.CreateArguments(
                "https://github.com/casbin/casbin-rs",
                GithubModule.Source.CODEXIA,
                "1662"
            )
        );
    }

    @Test
    @DataSet(
        value = "db-rider/github/github-module/initial/repoExistsByExternalId.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/github-module/expected/repoExistsByExternalId.yml")
    void repoExistsByExternalId() throws IOException {
        WireMockServer.stub(
            new Stub(
                "/github/repos/casbin/casbin-rs",
                new ResourceOf("wiremock/github/github-module/github/getRepo.json")
            )
        );

        module.createRepo(
            new GithubModule.CreateArguments(
                "https://github.com/casbin/casbin-rs",
                GithubModule.Source.CODEXIA,
                "1662"
            )
        );
    }

    @Test
    @DataSet(
        value = "db-rider/github/github-module/initial/repoExistsByCodexiaSource.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/github-module/expected/repoExistsByCodexiaSource.yml")
    void repoExistsByCodexiaSource() throws IOException {
        WireMockServer.stub(
            new Stub(
                "/github/repos/casbin/casbin-rs",
                new ResourceOf("wiremock/github/github-module/github/getRepo.json")
            )
        );

        module.createRepo(
            new GithubModule.CreateArguments(
                "https://github.com/casbin/casbin-rs",
                GithubModule.Source.CODEXIA,
                "1662"
            )
        );
    }

    @Test
    @DataSet(
        value = "db-rider/github/github-module/initial/notFoundInGithub.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/github/github-module/expected/notFoundInGithub.yml")
    void notFoundInGithub() throws IOException {
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
                new GithubModule.CreateArguments(
                    "https://github.com/casbin/casbin-rs",
                    GithubModule.Source.CODEXIA,
                    "1662"
                )
            )
        );
        assertEquals(
            exception.getMessage(),
            new FormattedText(
                "Unable to find github repo by name='%s'",
                "casbin/casbin-rs"
            ).asString()
        );
    }
}
