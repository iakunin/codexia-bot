package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.cactoos.Scalar;

@Slf4j
// @todo #92 write a unit-test for it
public final class ExactItem implements Scalar<Optional<Item>> {

    private final GithubApi githubStat;

    private final LinesOfCode linesOfCodeStat;

    public ExactItem(
        GithubApi githubStat,
        LinesOfCode linesOfCodeStat
    ) {
        this.githubStat = githubStat;
        this.linesOfCodeStat = linesOfCodeStat;
    }

    @Override
    public Optional<Item> value() {
        final Optional<Item> firstAttempt = linesOfCodeStat
            .getItemList()
            .stream()
            .filter(
                item -> item.getLanguage().toLowerCase().equals(
                    githubStat.getLanguage().toLowerCase()
                )
            )
            .findFirst();

        final Optional<Item> secondAttempt = linesOfCodeStat
            .getItemList()
            .stream()
            .filter(
                item ->
                    item.getLanguage().toLowerCase().contains(githubStat.getLanguage().toLowerCase())
                        ||
                    githubStat.getLanguage().toLowerCase().contains(item.getLanguage().toLowerCase())
            )
            .findFirst();

        return firstAttempt.or(() -> secondAttempt);
    }
}
