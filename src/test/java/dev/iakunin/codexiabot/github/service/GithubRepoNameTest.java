package dev.iakunin.codexiabot.github.service;

import dev.iakunin.codexiabot.github.GithubModule;
import java.net.URL;
import org.cactoos.text.FormattedText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class GithubRepoNameTest {

    @Test
    public void happyPath() throws Exception {
        assertEquals(
            "user/repo",
            new GithubRepoName(
                new URL("https://github.com/user/repo")
            ).value()
        );
    }

    @Test
    public void emptyPath() throws Exception {
        final GithubModule.InvalidRepoNameException exception = assertThrows(
            GithubModule.InvalidRepoNameException.class,
            () -> new GithubRepoName(
                new URL("https://github.com")
            ).value()
        );
        assertEquals(
            exception.getMessage(),
            new FormattedText(
                "Url with empty path given"
            ).asString()
        );
    }

    @Test
    public void pathWithOnlySlash() throws Exception {
        final GithubModule.InvalidRepoNameException exception = assertThrows(
            GithubModule.InvalidRepoNameException.class,
            () -> new GithubRepoName(
                new URL("https://github.com/")
            ).value()
        );
        assertEquals(
            exception.getMessage(),
            new FormattedText(
                "Url with empty path given"
            ).asString()
        );
    }

    @Test
    public void onlyOnePathItem() throws Exception {
        final GithubModule.InvalidRepoNameException exception = assertThrows(
            GithubModule.InvalidRepoNameException.class,
            () -> new GithubRepoName(
                new URL("https://github.com/user")
            ).value()
        );
        assertEquals(
            exception.getMessage(),
            new FormattedText(
                "Invalid github repository name: %s",
                "https://github.com/user"
            ).asString()
        );
    }

    @Test
    public void threePathSegments() throws Exception {
        assertEquals(
            "user/repo1",
            new GithubRepoName(
                new URL("https://github.com/user/repo1/repo2")
            ).value()
        );
    }

    @Test
    public void fourPathSegments() throws Exception {
        assertEquals(
            "user/first",
            new GithubRepoName(
                new URL("https://github.com/user/first/second/third")
            ).value()
        );
    }
}
