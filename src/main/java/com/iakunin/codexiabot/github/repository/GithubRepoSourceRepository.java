package com.iakunin.codexiabot.github.repository;

import com.iakunin.codexiabot.github.entity.GithubRepoSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepoSourceRepository extends JpaRepository<GithubRepoSource, Long> {
    /*_*/
}
