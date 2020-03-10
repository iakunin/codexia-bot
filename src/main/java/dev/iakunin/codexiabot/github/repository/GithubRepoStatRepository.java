package dev.iakunin.codexiabot.github.repository;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepoStatRepository extends JpaRepository<GithubRepoStat, Long> {
    /*_*/
}
