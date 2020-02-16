package com.iakunin.codexiabot.github.repository;

import com.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.List;
import java.util.Optional;
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
    List<GithubRepo> findAllWithoutLinesOfCode();
}
