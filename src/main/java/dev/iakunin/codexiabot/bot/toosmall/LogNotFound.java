package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.Optional;
import org.cactoos.Scalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogNotFound implements Scalar<Optional<Item>> {

    private final GithubRepoStat github;

    private final GithubRepoStat loc;

    private final Scalar<Optional<Item>> inner;

    private final Logger logger;

    public LogNotFound(
        final GithubRepoStat github,
        final GithubRepoStat loc,
        final Scalar<Optional<Item>> inner
    ) {
        this(
            github,
            loc,
            inner,
            LoggerFactory.getLogger(LogNotFound.class)
        );
    }

    /**
     * @checkstyle ParameterNumber (5 lines)
     */
    public LogNotFound(
        final GithubRepoStat github,
        final GithubRepoStat loc,
        final Scalar<Optional<Item>> inner,
        final Logger logger
    ) {
        this.github = github;
        this.loc = loc;
        this.inner = inner;
        this.logger = logger;
    }

    @Override
    public Optional<Item> value() throws Exception {
        final Optional<Item> result = this.inner.value();

        if (result.isEmpty()) {
            this.logger.debug(
                "Unable to find proper LoC stat; githubStatId='{}'; locStatId='{}'",
                this.github.getId(),
                this.loc.getId()
            );
        }

        return result;
    }
}
