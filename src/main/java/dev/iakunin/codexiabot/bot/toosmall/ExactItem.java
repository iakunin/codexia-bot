package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.cactoos.Func;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.func.FuncOf;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.scalar.EqualsNullable;
import org.cactoos.scalar.FirstOf;
import org.cactoos.scalar.Or;
import org.cactoos.text.Lowered;
import org.cactoos.text.NoNulls;

@Slf4j
public final class ExactItem implements Scalar<Optional<Item>> {

    private final Text language;

    private final Iterable<Item> items;

    public ExactItem(GithubApi githubStat, LinesOfCode linesOfCodeStat) {
        this(
            githubStat::getLanguage,
            linesOfCodeStat.getItemList()
        );
    }

    private ExactItem(Text language, Iterable<Item> items) {
        this.language = new NoNulls(language);
        this.items = new Filtered<>(
            Objects::nonNull,
            Optional.ofNullable(items).orElse(new IterableOf<>())
        );
    }

    @Override
    public Optional<Item> value() throws Exception {
        return
            new FirstOf<>(
                // @todo #92 replace with FirstOf with 2-arg-ctor when it's merged to cactoos
                new FuncOf<>(true),
                new Mapped<>(
                    Optional::of,
                    new Joined<>(
                        new Filtered<>(
                            new Exact(this.language),
                            this.items
                        ),
                        new Filtered<>(
                            new Approximate(this.language),
                            this.items
                        )
                    )
                ),
                Optional::<Item>empty
            ).value();
    }

    private static class Approximate implements Func<Item, Boolean> {

        private final Text reference;

        private Approximate(Text reference) {
            this.reference = new NoNulls(reference);
        }

        @Override
        public Boolean apply(Item item) throws Exception {
            return
                new FallbackFromNull<>(
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
                    () -> false
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

        private final Text reference;

        private Exact(Text text) {
            this.reference = new NoNulls(text);
        }

        @Override
        public Boolean apply(Item item) throws Exception {
            return
                new FallbackFromNull<>(
                    new EqualsNullable(
                        new Lowered(
                            new NoNulls(item::getLanguage)
                        ),
                        new Lowered(this.reference)
                    ),
                    () -> false
                ).value();
        }
    }
}
