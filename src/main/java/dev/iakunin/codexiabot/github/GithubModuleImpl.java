package dev.iakunin.codexiabot.github;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.factory.GithubRepoStatFactory;
import dev.iakunin.codexiabot.github.repository.GithubRepoRepository;
import dev.iakunin.codexiabot.github.repository.GithubRepoSourceRepository;
import dev.iakunin.codexiabot.github.repository.GithubRepoStatRepository;
import dev.iakunin.codexiabot.github.service.GithubRepoName;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cactoos.scalar.IoChecked;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public final class GithubModuleImpl implements GithubModule {

    private final GithubRepoRepository repo;

    private final GithubRepoStatRepository stat;

    private final GithubRepoSourceRepository source;

    private final GitHub github;

    @Override
    public void createRepo(final CreateArguments arguments) throws IOException {
        final GithubRepo rep = this.saveRepo(arguments);
        this.addRepoSource(
            new AddSourceArguments(
                rep,
                arguments.getSource(),
                arguments.getExternalId()
            )
        );
    }

    @Override
    public void addRepoSource(final AddSourceArguments arguments) {
        if (
            !this.source.existsByGithubRepoAndSourceAndExternalId(
                arguments.getGithubRepo(),
                arguments.getSource(),
                arguments.getExternalId()
            )
        ) {
            this.source.save(
                new GithubRepoSource()
                    .setGithubRepo(arguments.getGithubRepo())
                    .setSource(arguments.getSource())
                    .setExternalId(arguments.getExternalId())
            );
        }
    }

    @Override
    public void updateStat(final GithubRepo rep) throws IOException {
        this.stat.save(
            GithubRepoStatFactory
                .from(
                    this.github.getRepository(rep.getFullName())
                ).setGithubRepo(rep)
        );
    }

    @Override
    public void removeAllRepoSources(final DeleteArguments arguments) {
        log.debug("Removing all repo sources for {}", arguments);
        this.source.findAllBySourceAndExternalId(
            arguments.getSource(),
            arguments.getExternalId()
        ).forEach(
            this.source::delete
        );
    }

    @Override
    public Stream<GithubRepo> findAllInCodexia() {
        return this.repo.findAllInCodexia();
    }

    @Override
    public Stream<GithubRepo> findAllInCodexiaAndHackernews() {
        return this.repo.findAllInCodexiaAndHackernews();
    }

    @Override
    public Stream<GithubRepoSource> findAllRepoSources(final GithubRepo rep) {
        return this.source.findAllByGithubRepo(rep).stream();
    }

    @Override
    public Stream<GithubRepoSource> findAllRepoSources(final Source src) {
        return this.source.findAllBySource(src);
    }

    @Override
    public Stream<GithubRepoStat> findAllGithubApiStat(
        final GithubRepo rep,
        final Long id
    ) {
        return this.stat
            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(
                rep,
                GithubRepoStat.Type.GITHUB_API,
                id
            ).stream();
    }

    @Override
    public Optional<GithubRepoStat> findLastGithubApiStat(final GithubRepo rep) {
        return this.stat.findFirstByGithubRepoAndTypeOrderByIdDesc(
            rep,
            GithubRepoStat.Type.GITHUB_API
        );
    }

    @Override
    public Optional<GithubRepoStat> findLastLinesOfCodeStat(final GithubRepo rep) {
        return this.stat.findFirstByGithubRepoAndTypeOrderByIdDesc(
            rep,
            GithubRepoStat.Type.LINES_OF_CODE
        );
    }

    private GithubRepo saveRepo(final CreateArguments arguments) throws IOException {
        final String name = new IoChecked<>(
            new GithubRepoName(new URL(arguments.getUrl()))
        ).value();
        final GHRepository repository = this.tryToFindRepo(name);
        final GithubRepo entity = GithubRepo.Factory.from(repository);

        return this.repo
            .findByFullName(name)
            .or(
                () -> this.repo.findByExternalId(entity.getExternalId())
            ).orElseGet(
                () -> {
                    final GithubRepo saved = this.repo.save(entity);
                    this.stat.save(
                        GithubRepoStatFactory.from(repository).setGithubRepo(saved)
                    );
                    return saved;
                }
            );
    }

    private GHRepository tryToFindRepo(final String name) throws IOException {
        final var message = String.format("Unable to find github repo by name='%s'", name);

        try {
            return Optional.ofNullable(
                    this.github.getRepository(name)
                ).orElseThrow(
                    () -> new RepoNotFoundException(message)
                );
        } catch (final GHFileNotFoundException ex) {
            throw new RepoNotFoundException(message, ex);
        }
    }
}
