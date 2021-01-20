package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import org.cactoos.Fallback;
import org.cactoos.Func;
import org.cactoos.Text;
import org.cactoos.scalar.Or;
import org.cactoos.scalar.ScalarWithFallback;
import org.cactoos.text.Contains;
import org.cactoos.text.Lowered;
import org.cactoos.text.NoNulls;

final class Approximate implements Func<GithubRepoStat.LinesOfCode.Item, Boolean> {

    private final Text reference;

    Approximate(final Text reference) {
        this.reference = new NoNulls(reference);
    }

    @Override
    public Boolean apply(final GithubRepoStat.LinesOfCode.Item item) throws Exception {
        return
            new ScalarWithFallback<>(
                new Or(
                    new Contains(
                        new Lowered(
                            new NoNulls(item::getLanguage)
                        ),
                        new Lowered(this.reference)
                    ),
                    new Contains(
                        new Lowered(this.reference),
                        new Lowered(
                            new NoNulls(item::getLanguage)
                        )
                    )
                ),
                new Fallback.From<>(
                    RuntimeException.class,
                    ex -> false
                )
            ).value();
    }
}
