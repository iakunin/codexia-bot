package dev.iakunin.codexiabot.bot.entity;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;

public interface Result {

    GithubRepo getGithubRepo();

    GithubRepoStat getGithubRepoStat();
}
