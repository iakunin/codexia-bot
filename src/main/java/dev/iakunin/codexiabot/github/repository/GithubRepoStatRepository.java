package dev.iakunin.codexiabot.github.repository;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepoStatRepository extends JpaRepository<GithubRepoStat, Long> {

    List<GithubRepoStat> findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(
        GithubRepo repo,
        GithubRepoStat.Type type,
        Long id
    );

    Optional<GithubRepoStat> findFirstByGithubRepoAndTypeOrderByIdDesc(
        GithubRepo repo,
        GithubRepoStat.Type type
    );
}
