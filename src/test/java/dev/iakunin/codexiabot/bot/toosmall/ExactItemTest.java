package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.List;
import java.util.Optional;
import org.cactoos.list.ListOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ExactItemTest {

    private static final String LANGUAGE = "Java";

    @Test
    public void emptyItemList() {
        final Optional<Item> actual = new ExactItem(
            this.createGithub(ExactItemTest.LANGUAGE),
            this.createLinesOfCode(new ListOf<>())
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
    }

    @Test
    public void foundExact() {
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
    public void notFoundExact() {
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
    public void foundExactFirst() {
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
    public void foundExactLast() {
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
    public void foundApproximateThis() {
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
    public void foundApproximateThisFirst() {
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
    public void foundApproximateThisLast() {
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
    public void foundApproximateThat() {
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
    public void foundApproximateThatFirst() {
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
    public void foundApproximateThatLast() {
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
