package com.iakunin.codexiabot.github;

import java.io.IOException;

public interface GithubModule {
    void createRepo(String url) throws IOException;
}
