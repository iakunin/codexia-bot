package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.cactoos.Func;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.func.FuncOf;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.scalar.EqualsNullable;
import org.cactoos.scalar.FirstOf;
import org.cactoos.scalar.Or;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.Lowered;

@Slf4j
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
        return
            new Unchecked<>(
                new FirstOf<>(
                    // @todo #92 replace with FirstOf with 2-arg-ctor when it's merged to cactoos
                    new FuncOf<>(true),
                    new Mapped<>(
                        Optional::of,
                        new Joined<>(
                            new Filtered<>(
                                new Exact(this.githubStat.getLanguage()),
                                this.linesOfCodeStat.getItemList()
                            ),
                            new Filtered<>(
                                new Approximate(this.githubStat.getLanguage()),
                                this.linesOfCodeStat.getItemList()
                            )
                        )
                    ),
                    Optional::<Item>empty
                )
            ).value();
    }

    private static class Approximate implements Func<Item, Boolean> {

        private final String reference;

        private Approximate(String reference) {
            this.reference = reference;
        }

        @Override
        public Boolean apply(Item item) throws Exception {
            return
                new Or(
                    new Contains(
                        new Lowered(item.getLanguage()),
                        new Lowered(this.reference)
                    ),
                    new Contains(
                        new Lowered(this.reference),
                        new Lowered(item.getLanguage())
                    )
                ).value();
        }
    }

    // @todo #92 extract this class to cactoos and replace when it's merged
    private static class Contains implements Scalar<Boolean> {

        private final Text origin;

        private final Text other;

        private Contains(Text origin, Text other) {
            this.origin = origin;
            this.other = other;
        }

        @Override
        public Boolean value() throws Exception {
            return this.origin.asString().contains(this.other.asString());
        }
    }

    private static class Exact implements Func<Item, Boolean> {

        private final String reference;

        private Exact(String reference) {
            this.reference = reference;
        }

        @Override
        public Boolean apply(Item item) throws Exception {
            return
                new EqualsNullable(
                    new Lowered(item.getLanguage()),
                    new Lowered(this.reference)
                ).value();
        }
    }
}
