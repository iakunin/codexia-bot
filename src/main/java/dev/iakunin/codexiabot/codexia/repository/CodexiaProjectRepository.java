package dev.iakunin.codexiabot.codexia.repository;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodexiaProjectRepository extends JpaRepository<CodexiaProject, Long> {

    boolean existsByExternalId(Integer externalId);

    Optional<CodexiaProject> findByExternalId(Integer externalId);

    @Query(
        "select cp from CodexiaProject cp " +
        "left join GithubRepoSource grs " +
            "on cast (cp.externalId as string) = grs.externalId " +
            "and grs.source = 'CODEXIA' " +
        "where grs.id is null " +
        "and cp.deleted is null"
    )
    Stream<CodexiaProject> findAllActiveWithoutGithubRepo();

    @Query(
        "select cp from CodexiaProject cp " +
        "where cp.deleted is null"
    )
    Stream<CodexiaProject> findAllActive();

    @Query(
        "select cp from CodexiaProject cp " +
        "where cp.deleted is null"
    )
    Page<CodexiaProject> findAllActive(Pageable pageable);
}
