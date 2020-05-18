package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.List;
import java.util.Optional;
import org.cactoos.list.ListOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExactItemTest {

    private static final String LANGUAGE = "Java";

    @Test
    void emptyItemList() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(new ListOf<>())
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
    }

    @Test
    void foundExact() throws Exception {
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
    void notFoundExact() throws Exception {
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
    void foundExactFirst() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(ExactItemTest.LANGUAGE),
                    this.createItem("Kotlin")
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
    void foundExactLast() throws Exception {
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
    void foundApproximateThis() throws Exception {
        final String languageWithPostfix = ExactItemTest.LANGUAGE + " with some postfix";
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(languageWithPostfix)
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(languageWithPostfix)
            ),
            actual
        );
    }

    @Test
    void foundApproximateThisFirst() throws Exception {
        final String languageWithPostfix = ExactItemTest.LANGUAGE + " with some postfix";
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(languageWithPostfix),
                    this.createItem("Scala")
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(languageWithPostfix)
            ),
            actual
        );
    }

    @Test
    void foundApproximateThisLast() throws Exception {
        final String languageWithPostfix = ExactItemTest.LANGUAGE + " with some postfix";
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem("Scala"),
                    this.createItem(languageWithPostfix)
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem(languageWithPostfix)
            ),
            actual
        );
    }

    @Test
    void foundApproximateThat() throws Exception {
        final String languageWithPostfix = ExactItemTest.LANGUAGE + " with another postfix";
        final Optional<Item> actual = new ExactItem(
            this.createGithub(languageWithPostfix),
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
    void foundApproximateThatFirst() throws Exception {
        final String languageWithPostfix = ExactItemTest.LANGUAGE + " with another postfix";
        final Optional<Item> actual = new ExactItem(
            this.createGithub(languageWithPostfix),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem(ExactItemTest.LANGUAGE),
                    this.createItem("Ruby")
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
    void foundApproximateThatLast() throws Exception {
        final String languageWithPostfix = ExactItemTest.LANGUAGE + " with another postfix";
        final Optional<Item> actual = new ExactItem(
            this.createGithub(languageWithPostfix),
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
    void languageIsNull() throws Exception {
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
    void itemListWithNulls() throws Exception {
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
    void itemListOnlyNulls() throws Exception {
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
    void itemsWithNullLanguage() throws Exception {
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
    void nullInsteadOfItems() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(null)
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
    }

    @Test
    void foundWithMapping() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub("Vue"),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem("JavaScript")
                )
            )
        ).value();

        Assertions.assertEquals(
            Optional.of(
                this.createItem("JavaScript")
            ),
            actual
        );
    }

    @Test
    void notFoundWithMapping() throws Exception {
        final Optional<Item> actual = new ExactItem(
            this.createGithub("SomeAnotherLanguage"),
            this.createLinesOfCode(
                new ListOf<>(
                    this.createItem("JavaScript")
                )
            )
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
    }

    private GithubApi createGithub(String language) {
        return new GithubApi().setLanguage(language);
    }

    private LinesOfCode createLinesOfCode(List<Item> list) {
        return new LinesOfCode().setItemList(list);
    }

    private Item createItem(String language) {
        return new Item().setLanguage(language);
    }
}
