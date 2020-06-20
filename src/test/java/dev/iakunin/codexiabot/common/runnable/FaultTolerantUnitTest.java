package dev.iakunin.codexiabot.common.runnable;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.MoreThanOneLogger"})
public class FaultTolerantUnitTest {

    private static final String LOG_TEXT = "Error";

    @Test
    public void emptyStream() {
        final Logger logger = Mockito.mock(Logger.class);

        new FaultTolerant(
            Stream.empty(),
            tr -> logger.error(LOG_TEXT, tr.getCause())
        ).run();

        Mockito.verify(logger, Mockito.never()).error(Mockito.any());
    }

    @Test
    public void streamOfOne() {
        final Logger logger = Mockito.mock(Logger.class);

        new FaultTolerant(
            Stream.of(() -> { }),
            tr -> logger.error(LOG_TEXT, tr.getCause())
        ).run();

        Mockito.verify(logger, Mockito.never()).error(Mockito.any());
    }

    @Test
    public void streamOfTwo() {
        final Logger logger = Mockito.mock(Logger.class);

        new FaultTolerant(
            Stream.of(() -> { }, () -> { }),
            tr -> logger.error(LOG_TEXT, tr.getCause())
        ).run();

        Mockito.verify(logger, Mockito.never()).error(LOG_TEXT);
    }

    @Test
    public void streamWithException() {
        final Logger logger = Mockito.mock(Logger.class);
        final RuntimeException exception = new RuntimeException("Runtime");

        new FaultTolerant(
            Stream.of(
                () -> {
                    throw exception;
                }
            ),
            tr -> logger.error(LOG_TEXT, tr.getCause())
        ).run();

        Mockito.verify(logger, Mockito.times(1)).error(LOG_TEXT, exception);
    }

    @Test
    public void streamWithTwoExceptions() {
        final Logger logger = Mockito.mock(Logger.class);
        final RuntimeException exception = new RuntimeException("Another runtime");

        new FaultTolerant(
            Stream.of(
                () -> {
                    throw exception;
                },
                () -> { },
                () -> {
                    throw exception;
                },
                () -> { }
            ),
            tr -> logger.error(LOG_TEXT, tr.getCause())
        ).run();

        Mockito.verify(logger, Mockito.times(2)).error(LOG_TEXT, exception);
    }
}
