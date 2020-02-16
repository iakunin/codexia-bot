package com.iakunin.codexiabot.github;

import java.io.IOException;
import lombok.Data;

public interface GithubModule {

    void createRepo(Arguments arguments) throws IOException;

    @Data
    class Arguments {
        private String url;
        private Source source;
        private String externalId;
    }

    enum Source {
        HACKERNEWS,
        REDDIT,
    }
}
