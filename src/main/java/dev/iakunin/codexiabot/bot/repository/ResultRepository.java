package dev.iakunin.codexiabot.bot.repository;

import dev.iakunin.codexiabot.bot.entity.Result;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.Optional;

public interface ResultRepository {

    Optional<Result> findFirstByGithubRepoOrderByIdDesc(GithubRepo repo);

    Result save(Result result);
}
