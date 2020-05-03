package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

public final class LogNotFoundTest {

    private static final long GITHUB_STAT_ID = 22L;

    private static final long LINES_OF_CODE_STAT_ID = 33L;

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
        Mockito.verify(logger, Mockito.times(1)).info(
            "Unable to find proper LoC stat; githubStatId='{}'; locStatId='{}'",
            LogNotFoundTest.GITHUB_STAT_ID,
            LogNotFoundTest.LINES_OF_CODE_STAT_ID
        );
    }

    private GithubRepoStat createGithubStat() {
        final GithubRepoStat stat = new GithubRepoStat();
        stat.setId(LogNotFoundTest.GITHUB_STAT_ID);
        return stat;
    }

    private GithubRepoStat createLinesOfCodeStat() {
        final GithubRepoStat stat = new GithubRepoStat();
        stat.setId(LogNotFoundTest.LINES_OF_CODE_STAT_ID);
        return stat;
    }
}
