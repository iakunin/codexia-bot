package dev.iakunin.codexiabot.github.service;

import dev.iakunin.codexiabot.github.GithubModule;
import java.net.URL;
import org.cactoos.text.FormattedText;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class GithubRepoNameTest {

    @Test
    public void happyPath() throws Exception {
        Assertions.assertEquals(
            "user/repo",
            new GithubRepoName(
                new URL("https://github.com/user/repo")
            ).value()
        );
    }

    @Test
    public void emptyPath() throws Exception {
        final GithubModule.InvalidRepoNameException exception = Assertions.assertThrows(
            GithubModule.InvalidRepoNameException.class,
            () -> new GithubRepoName(
                new URL("https://github.com")
            ).value()
        );
        Assertions.assertEquals(
            exception.getMessage(),
            new FormattedText(
                "Url with empty path given"
            ).asString()
        );
    }

    @Test
    public void pathWithOnlySlash() throws Exception {
        final GithubModule.InvalidRepoNameException exception = Assertions.assertThrows(
            GithubModule.InvalidRepoNameException.class,
            () -> new GithubRepoName(
                new URL("https://github.com/")
            ).value()
        );
        Assertions.assertEquals(
            exception.getMessage(),
            new FormattedText(
                "Url with empty path given"
            ).asString()
        );
    }

    @Test
    public void onlyOnePathItem() throws Exception {
        final var url = "https://github.com/user";
        final GithubModule.InvalidRepoNameException exception = Assertions.assertThrows(
            GithubModule.InvalidRepoNameException.class,
            () -> new GithubRepoName(
                new URL(url)
            ).value()
        );
        Assertions.assertEquals(
            exception.getMessage(),
            new FormattedText(
                "Invalid github repository name: %s",
                url
            ).asString()
        );
    }

    @Test
    public void threePathSegments() throws Exception {
        Assertions.assertEquals(
            "user/repo1",
            new GithubRepoName(
                new URL("https://github.com/user/repo1/repo2")
            ).value()
        );
    }

    @Test
    public void fourPathSegments() throws Exception {
        Assertions.assertEquals(
            "user/first",
            new GithubRepoName(
                new URL("https://github.com/user/first/second/third")
            ).value()
        );
    }
}
