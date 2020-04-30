package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.cactoos.Scalar;

// @todo #92 write a unit-test for it
@Slf4j
public final class LogNotFound implements Scalar<Optional<Item>> {

    private final GithubApi githubStat;

    private final LinesOfCode linesOfCodeStat;

    private final Scalar<Optional<Item>> inner;

    public LogNotFound(
        GithubApi githubStat,
        LinesOfCode linesOfCodeStat,
        Scalar<Optional<Item>> inner
    ) {
        this.githubStat = githubStat;
        this.linesOfCodeStat = linesOfCodeStat;
        this.inner = inner;
    }

    @Override
    public Optional<Item> value() throws Exception {
        final Optional<Item> result = inner.value();

        result.ifPresentOrElse(
            item -> {},
            () -> log.warn(
                "Unable to find proper LoC stat; language='{}'; LoC list='{}'",
                githubStat.getLanguage(),
                linesOfCodeStat.getItemList()
            )
        );

        return result;
    }
}
