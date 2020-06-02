package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import org.cactoos.Func;
import org.cactoos.Text;
import org.cactoos.scalar.EqualsNullable;
import org.cactoos.scalar.ScalarWithFallback;
import org.cactoos.text.Lowered;
import org.cactoos.text.NoNulls;

final class Exact implements Func<GithubRepoStat.LinesOfCode.Item, Boolean> {

    private final Text reference;

    Exact(final Text text) {
        this.reference = new NoNulls(text);
    }

    @Override
    public Boolean apply(final GithubRepoStat.LinesOfCode.Item item) throws Exception {
        return
            new ScalarWithFallback<>(
                new EqualsNullable(
                    new Lowered(
                        new NoNulls(item::getLanguage)
                    ),
                    new Lowered(this.reference)
                ),
                RuntimeException.class,
                ex -> false
            ).value();
    }
}
