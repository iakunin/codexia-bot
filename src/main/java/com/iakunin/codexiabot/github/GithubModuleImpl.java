package com.iakunin.codexiabot.github;

import com.iakunin.codexiabot.github.entity.GithubRepo;
import com.iakunin.codexiabot.github.repository.GithubRepoRepository;
import java.io.IOException;
import java.net.URL;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor_={@Autowired})
@Slf4j
public final class GithubModuleImpl implements GithubModule {

    private GithubRepoRepository githubRepoRepository;

    @Override
    public void createRepo(String url) throws IOException {
        URL repoUrl = new URL(url);

        // @TODO: пересоздать этот токен и положить в env-переменную
        GitHub github = new GitHubBuilder().withOAuthToken("3e94b3e7b117c36cbc097f37e0a9233d605c5be9").build();

        final GHRepository repository;
        try {
            repository = github.getRepository(this.getGithubRepoName(repoUrl));
        } catch (GHFileNotFoundException e) {
            throw new RuntimeException(
                String.format("Unable to find github repo by url='%s'", url),
                e
            );
        }

        this.githubRepoRepository.save(
            GithubRepo.Factory.from(repository)
        );
    }

    private String getGithubRepoName(URL url) {
        String repoName;
        final String path = url.getPath();
        if (path.charAt(0) == '/') {
            repoName = path.substring(1);
        } else {
            repoName = path;
        }

        if (repoName.split("/").length != 2) {
            throw new RuntimeException(
                String.format("Invalid github repository name: %s", repoName)
            );
        }

        return repoName;
    }
}
