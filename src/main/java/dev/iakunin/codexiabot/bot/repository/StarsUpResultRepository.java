package dev.iakunin.codexiabot.bot.repository;

import dev.iakunin.codexiabot.bot.entity.StarsUpResult;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StarsUpResultRepository extends JpaRepository<StarsUpResult, Long> {

    Optional<StarsUpResult> findFirstByGithubRepoOrderByIdDesc(GithubRepo repo);
}
