package dev.iakunin.codexiabot.github.repository;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepoRepository extends JpaRepository<GithubRepo, Long> {

    Optional<GithubRepo> findByExternalId(String externalId);

    Optional<GithubRepo> findByFullName(String fullName);

    @Query(
        "select gr from GithubRepo gr " +
        "left join GithubRepoStat grs " +
        "on (gr = grs.githubRepo and grs.type = 'LINES_OF_CODE') " +
        "where grs.id is null"
    )
    Stream<GithubRepo> findAllWithoutLinesOfCode();

    @Query(
        value =
            "select distinct gr.* from github_repo gr " +
            "join github_repo_source grs1 on gr.id = grs1.github_repo_id " +
            "join github_repo_source grs2 on gr.id = grs2.github_repo_id " +
            "where grs1.source = 'CODEXIA' " +
            "and grs2.source = 'HACKERNEWS' " +
            "and grs1.deleted_at is null " +
            "and grs2.deleted_at is null",
        nativeQuery = true
    )
    Stream<GithubRepo> findAllInCodexiaAndHackernews();

    @Query(
        value =
            "select distinct gr.* from github_repo gr " +
            "join github_repo_source grs1 on gr.id = grs1.github_repo_id " +
            "where grs1.source = 'CODEXIA' " +
            "and grs1.deleted_at is null",
        nativeQuery = true
    )
    Stream<GithubRepo> findAllInCodexia();

    @Query("select gr from GithubRepo gr")
    Stream<GithubRepo> getAll();
}
