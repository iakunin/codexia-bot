package dev.iakunin.codexiabot.bot.repository;

import dev.iakunin.codexiabot.bot.entity.ForksUpResult;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForksUpResultRepository extends JpaRepository<ForksUpResult, Long> {

    Optional<ForksUpResult> findFirstByGithubRepoOrderByIdDesc(GithubRepo repo);
}
