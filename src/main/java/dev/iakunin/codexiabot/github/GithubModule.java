package dev.iakunin.codexiabot.github;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Value;

/**
 * @checkstyle MemberName (500 lines)
 * @checkstyle EmptyLineSeparator (500 lines)
 * @checkstyle VisibilityModifier (500 lines)
 */
public interface GithubModule {

    void createRepo(CreateArguments arguments) throws IOException;

    void addRepoSource(AddSourceArguments arguments);

    void updateStat(GithubRepo repo) throws IOException;

    void removeAllRepoSources(DeleteArguments arguments);

    /*
     * @todo #0 rewrite in much common way:
     *  `List<GithubRepo> findAllExistsInAllSources(Set<Source> sources)`
     *  and remove `findAllInCodexiaAndHackernews()` and `findAllInCodexia()` methods
     */
    Stream<GithubRepo> findAllInCodexiaAndHackernews();

    Stream<GithubRepo> findAllInCodexia();

    Stream<GithubRepoSource> findAllRepoSources(GithubRepo repo);

    Stream<GithubRepoSource> findAllRepoSources(GithubModule.Source source);

    Stream<GithubRepoStat> findAllGithubApiStat(GithubRepo repo, Long id);

    Optional<GithubRepoStat> findLastGithubApiStat(GithubRepo repo);

    Optional<GithubRepoStat> findLastLinesOfCodeStat(GithubRepo repo);

    @Value
    class CreateArguments {
        String url;
        Source source;
        String externalId;
    }

    @Value
    class AddSourceArguments {
        GithubRepo githubRepo;
        Source source;
        String externalId;
    }

    @Value
    class DeleteArguments {
        Source source;
        String externalId;
    }

    enum Source {
        HACKERNEWS,
        CODEXIA,
        REDDIT,
    }

    @SuppressWarnings("PMD.FieldNamingConventions")
    final class InvalidRepoNameException extends IOException {
        private static final long serialVersionUID = 1048258389765098468L;



        public InvalidRepoNameException(final String message) {
            super(message);
        }
    }

    @SuppressWarnings("PMD.FieldNamingConventions")
    final class RepoNotFoundException extends IOException {
        private static final long serialVersionUID = 3354699865066905941L;

        public RepoNotFoundException(final String message) {
            super(message);
        }

        public RepoNotFoundException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
