package dev.iakunin.codexiabot.github.repository;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepoSourceRepository extends JpaRepository<GithubRepoSource, Long> {

    boolean existsByGithubRepoAndSourceAndExternalId(
        GithubRepo repo,
        GithubModule.Source source,
        String id
    );

    List<GithubRepoSource> findAllByGithubRepo(GithubRepo repo);

    List<GithubRepoSource> findAllBySourceAndExternalId(GithubModule.Source source, String id);

    Stream<GithubRepoSource> findAllBySource(GithubModule.Source source);

    //!!! Remember about `deleted_at` column !!!
}
