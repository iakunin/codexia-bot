package com.iakunin.codexiabot.github.repository;

import com.iakunin.codexiabot.github.entity.GithubRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepoRepository extends JpaRepository<GithubRepo, Long> {
    /*_*/
}
