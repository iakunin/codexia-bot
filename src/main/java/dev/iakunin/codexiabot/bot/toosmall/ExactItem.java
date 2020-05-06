package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.cactoos.Func;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.EqualsNullable;
import org.cactoos.scalar.FirstOf;
import org.cactoos.scalar.Or;
import org.cactoos.scalar.ScalarWithFallback;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.Contains;
import org.cactoos.text.Lowered;
import org.cactoos.text.NoNulls;
import org.cactoos.text.TextEnvelope;

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
            new FirstOf<Optional<Item>>(
                new Mapped<>(
                    Optional::of,
                    new Joined<>(
                        new Filtered<>(
                            new Exact(this.language),
                            this.items
                        ),
                        new Filtered<>(
                            new Exact(
                                new MappedText(this.language)
                            ),
                            this.items
                        ),
                        new Filtered<>(
                            new Approximate(this.language),
                            this.items
                        )
                    )
                ),
                Optional::empty
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
                    RuntimeException.class,
                    ex -> false
                ).value();
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

    private static class MappedText extends TextEnvelope {

        private MappedText(Text source) {
            this(
                source,
                new MapOf<>(
                    new MapEntry<>("Vue", "JavaScript")
                )
            );
        }

        private MappedText(Text source, Map<String, String> map) {
            super(
                new Unchecked<>(
                    () -> map.getOrDefault(
                        new NoNulls(source).toString(),
                        new NoNulls(source).toString()
                    )
                )
            );
        }
    }
}
