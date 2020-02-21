package com.iakunin.codexiabot.github;

import com.iakunin.codexiabot.github.entity.GithubRepo;
import com.iakunin.codexiabot.github.entity.GithubRepoSource;
import com.iakunin.codexiabot.github.entity.GithubRepoStat;
import com.iakunin.codexiabot.github.repository.GithubRepoRepository;
import com.iakunin.codexiabot.github.repository.GithubRepoSourceRepository;
import com.iakunin.codexiabot.github.repository.GithubRepoStatRepository;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public final class GithubModuleImpl implements GithubModule {

    private GithubRepoRepository githubRepoRepository;

    private GithubRepoStatRepository githubRepoStatRepository;

    private GithubRepoSourceRepository githubRepoSourceRepository;

    private String githubToken;

    public GithubModuleImpl(
        GithubRepoRepository githubRepoRepository,
        GithubRepoStatRepository githubRepoStatRepository,
        GithubRepoSourceRepository githubRepoSourceRepository,
        @Value("${app.github-token}") String githubToken
    ) {
        this.githubRepoRepository = githubRepoRepository;
        this.githubRepoStatRepository = githubRepoStatRepository;
        this.githubRepoSourceRepository = githubRepoSourceRepository;
        this.githubToken = githubToken;
    }

    @Override
    public void createRepo(Arguments arguments) throws IOException {
        URL repoUrl = new URL(arguments.getUrl());

        GitHub github = new GitHubBuilder().withOAuthToken(this.githubToken).build();

        //@TODO: кейс, когда github_repo есть, НО source - новый
        //@TODO: отрефакторить этот класс (слишком жирный)
        final GHRepository repository;
        try {
            final String githubRepoName = this.getGithubRepoName(repoUrl);

            final Optional<GithubRepo> optional = this.githubRepoRepository.findByFullName(githubRepoName);
            if (optional.isPresent()) {
                log.info("githubRepo with FullName='{}' already exist", githubRepoName);
                return;
            }

            log.info("--- Begin calling GitHub SDK");
            repository = github.getRepository(githubRepoName);
            log.info("--- End calling GitHub SDK");
        } catch (GHFileNotFoundException e) {
            throw new RuntimeException(
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
            this.githubRepoSourceRepository.save(
                new GithubRepoSource()
                    .setGithubRepo(saved)
                    .setSource(arguments.getSource())
                    .setExternalId(arguments.getExternalId())
            );
        } else {
            log.info("githubRepo with externalId='{}' already exist", githubRepo.getExternalId());
        }
    }

    private String getGithubRepoName(URL url) {
        String repoName;
        final String path = url.getPath();
        if (path.charAt(0) == '/') {
            repoName = path.substring(1);
        } else {
            repoName = path;
        }

        final String[] split = repoName.split("/");
        if (split.length < 2) {
            throw new RuntimeException(
                String.format("Invalid github repository name: %s", repoName)
            );
        }

        return Arrays.stream(split).limit(2).collect(Collectors.joining("/"));
    }
}
