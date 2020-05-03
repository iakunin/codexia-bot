package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.Optional;
import org.cactoos.Scalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogNotFound implements Scalar<Optional<Item>> {

    private final GithubRepoStat github;

    private final GithubRepoStat linesOfCode;

    private final Scalar<Optional<Item>> inner;

    private final Logger logger;

    public LogNotFound(
        GithubRepoStat github,
        GithubRepoStat linesOfCode,
        Scalar<Optional<Item>> inner
    ) {
        this(
            github,
            linesOfCode,
            inner,
            LoggerFactory.getLogger(LogNotFound.class)
        );
    }

    public LogNotFound(
        GithubRepoStat github,
        GithubRepoStat linesOfCode,
        Scalar<Optional<Item>> inner,
        Logger logger
    ) {
        this.github = github;
        this.linesOfCode = linesOfCode;
        this.inner = inner;
        this.logger = logger;
    }

    @Override
    public Optional<Item> value() throws Exception {
        final Optional<Item> result = this.inner.value();

        if (result.isEmpty()) {
            this.logger.warn(
                "Unable to find proper LoC stat; githubStatId='{}'; locStatId='{}'",
                github.getId(),
                linesOfCode.getId()
            );
        }

        return result;
    }
}
