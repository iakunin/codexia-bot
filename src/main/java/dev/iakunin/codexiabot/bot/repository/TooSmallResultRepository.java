package dev.iakunin.codexiabot.bot.repository;

import dev.iakunin.codexiabot.bot.entity.TooSmallResult;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TooSmallResultRepository extends JpaRepository<TooSmallResult, Long> {

    Optional<TooSmallResult> findFirstByGithubRepoOrderByIdDesc(GithubRepo repo);
}
