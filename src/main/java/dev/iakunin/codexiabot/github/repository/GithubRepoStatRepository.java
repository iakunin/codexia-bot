package dev.iakunin.codexiabot.github.repository;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import java.util.LinkedList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepoStatRepository extends JpaRepository<GithubRepoStat, Long> {

    LinkedList<GithubRepoStat> findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(
        GithubRepo repo,
        GithubRepoStat.Type type,
        Long id
    );
}
