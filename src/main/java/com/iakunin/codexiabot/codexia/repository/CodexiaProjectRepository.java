package com.iakunin.codexiabot.codexia.repository;

import com.iakunin.codexiabot.codexia.entity.CodexiaProject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodexiaProjectRepository extends JpaRepository<CodexiaProject, Long> {
    Boolean existsByExternalId(Integer externalId);

    @Query(
        "select cp from CodexiaProject cp " +
        "left join GithubRepoSource grs " +
            "on cast (cp.externalId as string) = grs.externalId " +
            "and grs.source = 'CODEXIA' " +
        "where grs.id is null"
    )
    List<CodexiaProject> findAllWithoutGithubProject();
}
