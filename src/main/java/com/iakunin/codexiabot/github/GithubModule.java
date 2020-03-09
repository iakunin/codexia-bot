package com.iakunin.codexiabot.github;

import com.iakunin.codexiabot.github.entity.GithubRepo;
import com.iakunin.codexiabot.github.entity.GithubRepoSource;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Data;

public interface GithubModule {

    void createRepo(CreateArguments arguments) throws IOException;

    void removeAllRepoSources(DeleteArguments arguments);

    //@TODO: rewrite in much common way: `List<GithubRepo> findAllExistsInAllSources(Set<Source> sources)`
    Set<GithubRepo> findAllInCodexiaAndHackernews();

    Set<GithubRepoSource> findAllRepoSources(GithubRepo repo);

    Stream<GithubRepoSource> findAllRepoSources(GithubModule.Source source);

    @Data
    class CreateArguments {
        private String url;
        private Source source;
        private String externalId;
    }

    @Data
    class DeleteArguments {
        private Source source;
        private String externalId;
    }

    enum Source {
        HACKERNEWS,
        CODEXIA,
        REDDIT,
    }
}
