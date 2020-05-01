package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.Optional;
import org.cactoos.Scalar;
import org.slf4j.Logger;

public final class LogNotFound implements Scalar<Optional<Item>> {

    private final GithubApi githubStat;

    private final LinesOfCode linesOfCodeStat;

    private final Scalar<Optional<Item>> inner;

    private final Logger logger;

    public LogNotFound(
        GithubApi githubStat,
        LinesOfCode linesOfCodeStat,
        Scalar<Optional<Item>> inner,
        Logger logger
    ) {
        this.githubStat = githubStat;
        this.linesOfCodeStat = linesOfCodeStat;
        this.inner = inner;
        this.logger = logger;
    }

    @Override
    public Optional<Item> value() throws Exception {
        final Optional<Item> result = this.inner.value();

        if (result.isEmpty()) {
            this.logger.warn(
                "Unable to find proper LoC stat; language='{}'; LoC list='{}'",
                githubStat.getLanguage(),
                linesOfCodeStat.getItemList()
            );
        }

        return result;
    }
}
