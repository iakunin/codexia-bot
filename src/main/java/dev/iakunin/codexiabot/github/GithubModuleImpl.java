package dev.iakunin.codexiabot.github;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
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

    private final GithubRepoRepository githubRepoRepository;
    private final GithubRepoStatRepository githubRepoStatRepository;
    private final GithubRepoSourceRepository githubRepoSourceRepository;
    private final GitHub github;

    @Override
    public void createRepo(CreateArguments arguments) throws IOException {
        final GithubRepo githubRepo = this.saveRepo(arguments);
        this.addRepoSource(
            new AddSourceArguments(
                githubRepo,
                arguments.getSource(),
                arguments.getExternalId()
            )
        );
    }

    @Override
    public void addRepoSource(AddSourceArguments arguments) {
        if (
            !this.githubRepoSourceRepository.existsByGithubRepoAndSourceAndExternalId(
                arguments.getGithubRepo(),
                arguments.getSource(),
                arguments.getExternalId()
            )
        ) {
            this.githubRepoSourceRepository.save(
                new GithubRepoSource()
                    .setGithubRepo(arguments.getGithubRepo())
                    .setSource(arguments.getSource())
                    .setExternalId(arguments.getExternalId())
            );
        }
    }

    @Override
    public void updateStat(GithubRepo githubRepo) throws IOException {
        this.githubRepoStatRepository.save(
            GithubRepoStat.Factory
                .from(
                    this.github.getRepository(githubRepo.getFullName())
                ).setGithubRepo(githubRepo)
        );
    }

    @Override
    public void removeAllRepoSources(DeleteArguments arguments) {
        log.debug("Removing all repo sources for {}", arguments);
        this.githubRepoSourceRepository.findAllBySourceAndExternalId(
            arguments.getSource(),
            arguments.getExternalId()
        ).forEach(
            this.githubRepoSourceRepository::delete
        );
    }

    @Override
    public Stream<GithubRepo> findAllInCodexia() {
        return this.githubRepoRepository.findAllInCodexia();
    }

    @Override
    public Stream<GithubRepo> findAllInCodexiaAndHackernews() {
        return this.githubRepoRepository.findAllInCodexiaAndHackernews();
    }

    @Override
    public Stream<GithubRepoSource> findAllRepoSources(GithubRepo repo) {
        return this.githubRepoSourceRepository.findAllByGithubRepo(repo).stream();
    }

    @Override
    public Stream<GithubRepoSource> findAllRepoSources(Source source) {
        return this.githubRepoSourceRepository.findAllBySource(source);
    }

    @Override
    public Stream<GithubRepoStat> findAllGithubApiStat(GithubRepo repo, Long idGreaterThan) {
        return this.githubRepoStatRepository.findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(
            repo,
            GithubRepoStat.Type.GITHUB_API,
            idGreaterThan
        ).stream();
    }

    @Override
    public Optional<GithubRepoStat> findLastGithubApiStat(GithubRepo repo) {
        return this.githubRepoStatRepository.findFirstByGithubRepoAndTypeOrderByIdDesc(
            repo,
            GithubRepoStat.Type.GITHUB_API
        );
    }

    @Override
    public Optional<GithubRepoStat> findLastLinesOfCodeStat(GithubRepo repo) {
        return this.githubRepoStatRepository.findFirstByGithubRepoAndTypeOrderByIdDesc(
            repo,
            GithubRepoStat.Type.LINES_OF_CODE
        );
    }

    private GithubRepo saveRepo(CreateArguments arguments) throws IOException {
        final String githubRepoName = new IoChecked<>(new GithubRepoName(new URL(arguments.getUrl()))).value();
        final GHRepository repository = this.tryToFindRepo(githubRepoName);
        final GithubRepo githubRepo = GithubRepo.Factory.from(repository);

        return this.githubRepoRepository
            .findByFullName(githubRepoName)
            .or(
                () -> this.githubRepoRepository.findByExternalId(githubRepo.getExternalId())
            ).orElseGet(
                () -> {
                    final GithubRepo saved = this.githubRepoRepository.save(githubRepo);
                    this.githubRepoStatRepository.save(
                        GithubRepoStat.Factory.from(repository).setGithubRepo(saved)
                    );
                    return saved;
                }
            );
    }

    private GHRepository tryToFindRepo(String githubRepoName) throws IOException {
        try {
            return this.github.getRepository(githubRepoName);
        } catch (GHFileNotFoundException e) {
            throw new RepoNotFoundException(
                String.format("Unable to find github repo by name='%s'", githubRepoName),
                e
            );
        }
    }
}
