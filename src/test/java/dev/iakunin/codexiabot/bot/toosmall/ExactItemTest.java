package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.List;
import java.util.Optional;
import org.cactoos.list.ListOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExactItemTest {

    private static final String LANGUAGE = "Java";

    private static final String POSTFIX = "some random postfix";

    @Test
    public void emptyItemList() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(new ListOf<>())
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
    }

    @Test
    public void foundExact() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(ExactItemTest.LANGUAGE)
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(ExactItemTest.LANGUAGE)
            ),
            actual
        );
    }

    @Test
    public void notFoundExact() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub("Other language"),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(ExactItemTest.LANGUAGE)
                )
            )
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
    }

    @Test
    public void foundExactFirst() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(ExactItemTest.LANGUAGE),
                    this.createItem("Groovy")
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(ExactItemTest.LANGUAGE)
            ),
            actual
        );
    }

    @Test
    public void foundExactLast() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem("Kotlin"),
                    this.createItem(ExactItemTest.LANGUAGE)
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(ExactItemTest.LANGUAGE)
            ),
            actual
        );
    }

    @Test
    public void foundApproximateThis() throws Exception {
        final String postfixed = ExactItemTest.LANGUAGE + ExactItemTest.POSTFIX;
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(postfixed)
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(postfixed)
            ),
            actual
        );
    }

    @Test
    public void foundApproximateThisFirst() throws Exception {
        final String postfixed = ExactItemTest.LANGUAGE + ExactItemTest.POSTFIX;
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(postfixed),
                    this.createItem("Erlang")
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(postfixed)
            ),
            actual
        );
    }

    @Test
    public void foundApproximateThisLast() throws Exception {
        final String postfixed = ExactItemTest.LANGUAGE + ExactItemTest.POSTFIX;
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem("Scala"),
                    this.createItem(postfixed)
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(postfixed)
            ),
            actual
        );
    }

    @Test
    public void foundApproximateThat() throws Exception {
        final String postfixed = ExactItemTest.LANGUAGE + ExactItemTest.POSTFIX;
        final Optional<Item> actual = new ExactItem(
            this.createGithub(postfixed),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(ExactItemTest.LANGUAGE)
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(ExactItemTest.LANGUAGE)
            ),
            actual
        );
    }

    @Test
    public void foundApproximateThatFirst() throws Exception {
        final String postfixed = ExactItemTest.LANGUAGE + ExactItemTest.POSTFIX;
        final Optional<Item> actual = new ExactItem(
            this.createGithub(postfixed),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(ExactItemTest.LANGUAGE),
                    this.createItem("C++")
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(ExactItemTest.LANGUAGE)
            ),
            actual
        );
    }

    @Test
    public void foundApproximateThatLast() throws Exception {
        final String postfixed = ExactItemTest.LANGUAGE + ExactItemTest.POSTFIX;
        final Optional<Item> actual = new ExactItem(
            this.createGithub(postfixed),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem("Ruby"),
                    this.createItem(ExactItemTest.LANGUAGE)
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(ExactItemTest.LANGUAGE)
            ),
            actual
        );
    }

    @Test
    public void languageIsNull() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(null),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(ExactItemTest.LANGUAGE)
                )
            )
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
    }

    @Test
    public void itemListWithNulls() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    null,
                    this.createItem(ExactItemTest.LANGUAGE),
                    null
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(ExactItemTest.LANGUAGE)
            ),
            actual
        );
    }

    @Test
    public void itemListOnlyNulls() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    null,
                    null
                )
            )
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
    }

    @Test
    public void itemsWithNullLanguage() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(null),
                    this.createItem(null)
                )
            )
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
    }

    @Test
    public void nullInsteadOfItems() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(null)
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
    }

    @Test
    public void foundWithMapping() throws Exception {
        final var source = "Vue";
        final var destination = "JavaScript";
        final Optional<Item> actual = new ExactItem(
            this.createGithub(source),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(destination)
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(destination)
            ),
            actual
        );
    }

    @Test
    public void notFoundWithMapping() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub("SomeAnotherLanguage"),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem("Rust")
                )
            )
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
    }

    private GithubApi createGithub(final String language) {
        return new GithubApi().setLanguage(language);
    }

    private LinesOfCode createLinesOfCode(final List<Item> list) {
        return new LinesOfCode().setItemList(list);
    }

    private Item createItem(final String language) {
        return new Item().setLanguage(language);
    }
}
