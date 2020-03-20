package dev.iakunin.codexiabot.github;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Data;

public interface GithubModule {

    void createRepo(CreateArguments arguments) throws IOException;

    void removeAllRepoSources(DeleteArguments arguments);

    //@TODO: rewrite in much common way: `List<GithubRepo> findAllExistsInAllSources(Set<Source> sources)`
    Set<GithubRepo> findAllInCodexiaAndHackernews();

    //@TODO: rewrite in much common way: `List<GithubRepo> findAllExistsInAllSources(Set<Source> sources)`
    Set<GithubRepo> findAllInCodexia();

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

    final class InvalidRepoNameException extends IOException {
        public InvalidRepoNameException(String message) {
            super(message);
        }
    }

    final class RepoNotFoundException extends IOException {
        public RepoNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
