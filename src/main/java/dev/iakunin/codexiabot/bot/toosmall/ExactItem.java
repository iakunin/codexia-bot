package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.scalar.FirstOf;
import org.cactoos.text.NoNulls;

/**
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@Slf4j
public final class ExactItem implements Scalar<Optional<Item>> {

    private final Text language;

    private final Iterable<Item> items;

    public ExactItem(final GithubApi github, final LinesOfCode loc) {
        this(
            github::getLanguage,
            loc.getItemList()
        );
    }

    private ExactItem(final Text language, final Iterable<Item> items) {
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
}
