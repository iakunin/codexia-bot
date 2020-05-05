package dev.iakunin.codexiabot.github.service;

import dev.iakunin.codexiabot.github.entity.GithubRepo;

public interface LinesOfCode {

    void calculate(GithubRepo repo);
}
