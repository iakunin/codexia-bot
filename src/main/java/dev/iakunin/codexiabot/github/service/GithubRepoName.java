package dev.iakunin.codexiabot.github.service;

import dev.iakunin.codexiabot.github.GithubModule;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.cactoos.Scalar;

public final class GithubRepoName implements Scalar<String> {

    private final URL url;

    public GithubRepoName(URL url) {
        this.url = url;
    }

    @Override
    public String value() throws Exception {
        final String path = this.url.getPath();
        if (path.length() == 0 || "/".equals(path)) {
            throw new GithubModule.InvalidRepoNameException(
                "Url with empty path given"
            );
        }

        final String[] split = path.substring(1).split("/");
        if (split.length < 2) {
            throw new GithubModule.InvalidRepoNameException(
                String.format("Invalid github repository name: %s", this.url.toString())
            );
        }

        return Arrays.stream(split).limit(2).collect(Collectors.joining("/"));
    }
}
