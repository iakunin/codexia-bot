package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.List;
import java.util.Optional;
import org.cactoos.list.ListOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

public final class LogNotFoundTest {

    private static final String LANGUAGE = "some test language";

    private static final List<Item> ITEM_LIST = new ListOf<>();

    @Test
    public void innerIsPresent() throws Exception {
        final Logger logger = Mockito.mock(Logger.class);

        final Optional<Item> actual = new LogNotFound(
            this.createGithubStat(),
            this.createLinesOfCodeStat(),
            () -> Optional.of(new Item()),
            logger
        ).value();

        Assertions.assertEquals(Optional.of(new Item()), actual);
        Mockito.verify(logger, Mockito.never()).warn(Mockito.any());
    }

    @Test
    public void innerIsEmpty() throws Exception {
        final Logger logger = Mockito.mock(Logger.class);

        final Optional<Item> actual = new LogNotFound(
            this.createGithubStat(),
            this.createLinesOfCodeStat(),
            Optional::empty,
            logger
        ).value();

        Assertions.assertEquals(Optional.empty(), actual);
        Mockito.verify(logger, Mockito.times(1)).warn(
            "Unable to find proper LoC stat; language='{}'; LoC list='{}'",
            LogNotFoundTest.LANGUAGE,
            LogNotFoundTest.ITEM_LIST
        );
    }

    private GithubApi createGithubStat() {
        return new GithubApi().setLanguage(LogNotFoundTest.LANGUAGE);
    }

    private LinesOfCode createLinesOfCodeStat() {
        return new LinesOfCode().setItemList(LogNotFoundTest.ITEM_LIST);
    }
}
