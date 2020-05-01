package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.stream.Stream;

public interface Bot {

    Stream<GithubRepo> repoStream();
}
