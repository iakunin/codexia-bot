package dev.iakunin.codexiabot.github;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import java.io.IOException;
import java.util.Deque;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Data;

public interface GithubModule {

    void createRepo(CreateArguments arguments) throws IOException;

    void updateStat(GithubRepo githubRepo) throws IOException;

    void removeAllRepoSources(DeleteArguments arguments);

    /*
     * @todo #0 rewrite in much common way: `List<GithubRepo> findAllExistsInAllSources(Set<Source> sources)`
     *  and remove `findAllInCodexiaAndHackernews()` and `findAllInCodexia()` methods
     */
    Set<GithubRepo> findAllInCodexiaAndHackernews();

    Set<GithubRepo> findAllInCodexia();

    Set<GithubRepoSource> findAllRepoSources(GithubRepo repo);

    Stream<GithubRepoSource> findAllRepoSources(GithubModule.Source source);

    Deque<GithubRepoStat> findAllGithubApiStat(GithubRepo repo, Long idGreaterThan);

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
