package dev.iakunin.codexiabot.github;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.repository.GithubRepoRepository;
import dev.iakunin.codexiabot.github.repository.GithubRepoSourceRepository;
import dev.iakunin.codexiabot.github.repository.GithubRepoStatRepository;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class GithubModuleImpl implements GithubModule {

    private final GithubRepoRepository githubRepoRepository;
    private final GithubRepoStatRepository githubRepoStatRepository;
    private final GithubRepoSourceRepository githubRepoSourceRepository;
    private final GitHub github;

    @Override
    public void createRepo(CreateArguments arguments) throws IOException {
        final GithubRepo githubRepo = this.saveRepo(arguments);
        this.saveRepoSource(arguments, githubRepo);
    }

    public void updateStat(GithubRepo githubRepo) throws IOException {
        log.info("--- Begin calling GitHub SDK");
        final GHRepository repository = this.github.getRepository(githubRepo.getFullName());
        log.info("--- End calling GitHub SDK");

        this.githubRepoStatRepository.save(
            GithubRepoStat.Factory.from(repository).setGithubRepo(githubRepo)
        );
    }

    @Override
    public void removeAllRepoSources(DeleteArguments arguments) {
        log.info("Removing all repo sources for {}", arguments);
        this.githubRepoSourceRepository.findAllBySourceAndExternalId(
            arguments.getSource(),
            arguments.getExternalId()
        ).forEach(
            this.githubRepoSourceRepository::delete
        );
    }

    @Override
    public Set<GithubRepo> findAllInCodexia() {
        return this.githubRepoRepository.findAllInCodexia();
    }

    @Override
    public Set<GithubRepo> findAllInCodexiaAndHackernews() {
        return this.githubRepoRepository.findAllInCodexiaAndHackernews();
    }

    @Override
    public Set<GithubRepoSource> findAllRepoSources(GithubRepo repo) {
        return this.githubRepoSourceRepository.findAllByGithubRepo(repo);
    }

    @Override
    public Stream<GithubRepoSource> findAllRepoSources(Source source) {
        return this.githubRepoSourceRepository.findAllBySource(source);
    }

    @Override
    public Deque<GithubRepoStat> findAllGithubApiStat(GithubRepo repo, Long idGreaterThan) {
        return this.githubRepoStatRepository.findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(
            repo,
            GithubRepoStat.Type.GITHUB_API,
            idGreaterThan
        );
    }

    @Override
    public Optional<GithubRepoStat> findLastGithubApiStat(GithubRepo repo) {
        return this.githubRepoStatRepository.findFirstByGithubRepoAndTypeOrderByIdDesc(
            repo,
            GithubRepoStat.Type.GITHUB_API
        );
    }

    private GithubRepo saveRepo(CreateArguments arguments) throws IOException {
        URL repoUrl = new URL(arguments.getUrl());

        final GHRepository repository;
        try {
            final String githubRepoName = this.getGithubRepoName(repoUrl);

            final Optional<GithubRepo> optional = this.githubRepoRepository.findByFullName(githubRepoName);
            if (optional.isPresent()) {
                log.info("githubRepo with FullName='{}' already exist", githubRepoName);

                return optional.get();
            }

            log.info("--- Begin calling GitHub SDK");
            repository = this.github.getRepository(githubRepoName);
            log.info("--- End calling GitHub SDK");
        } catch (GHFileNotFoundException e) {
            throw new RepoNotFoundException(
                String.format("Unable to find github repo by url='%s'", arguments.getUrl()),
                e
            );
        }

        final GithubRepo githubRepo = GithubRepo.Factory.from(repository);
        final Optional<GithubRepo> optional = this.githubRepoRepository.findByExternalId(githubRepo.getExternalId());
        if (optional.isEmpty()) {
            log.info("Saving githubRepo: {}", githubRepo);
            final GithubRepo saved = this.githubRepoRepository.save(githubRepo);
            this.githubRepoStatRepository.save(
                GithubRepoStat.Factory.from(repository).setGithubRepo(saved)
            );

            return saved;
        } else {
            log.info("githubRepo with externalId='{}' already exist", githubRepo.getExternalId());

            return optional.get();
        }
    }

    private void saveRepoSource(CreateArguments arguments, GithubRepo githubRepo) {
        if (
            !this.githubRepoSourceRepository.existsByGithubRepoAndSourceAndExternalId(
                githubRepo,
                arguments.getSource(),
                arguments.getExternalId()
            )
        ) {
            log.info(
                "Saving GithubRepoSource for githubRepoId='{}'; source='{}', externalId='{}'",
                githubRepo.getId(),
                arguments.getSource(),
                arguments.getExternalId()
            );
            this.githubRepoSourceRepository.save(
                new GithubRepoSource()
                    .setGithubRepo(githubRepo)
                    .setSource(arguments.getSource())
                    .setExternalId(arguments.getExternalId())
            );
            return;
        }

        log.info(
            "Skip saving GithubRepoSource for githubRepoId='{}'; source='{}', externalId='{}'",
            githubRepo.getId(),
            arguments.getSource(),
            arguments.getExternalId()
        );
    }

    private String getGithubRepoName(URL url) throws InvalidRepoNameException {
        String repoName;
        final String path = url.getPath();
        if (path.charAt(0) == '/') {
            repoName = path.substring(1);
        } else {
            repoName = path;
        }

        final String[] split = repoName.split("/");
        if (split.length < 2) {
            throw new InvalidRepoNameException(
                String.format("Invalid github repository name: %s", repoName)
            );
        }

        return Arrays.stream(split).limit(2).collect(Collectors.joining("/"));
    }
}
