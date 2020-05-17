package dev.iakunin.codexiabot.common.runnable;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

class FaultTolerantUnitTest {

    @Test
    void emptyStream() {
        final Logger logger = Mockito.mock(Logger.class);

        new FaultTolerant(
            Stream.empty(),
            tr -> logger.error("Error", tr.getCause())
        ).run();

        Mockito.verify(logger, Mockito.never()).error(Mockito.any());
    }

    @Test
    void streamOfOne() {
        final Logger logger = Mockito.mock(Logger.class);

        new FaultTolerant(
            Stream.of(() -> {}),
            tr -> logger.error("Error", tr.getCause())
        ).run();

        Mockito.verify(logger, Mockito.never()).error(Mockito.any());
    }

    @Test
    void streamOfTwo() {
        final Logger logger = Mockito.mock(Logger.class);

        new FaultTolerant(
            Stream.of(() -> {}, () -> {}),
            tr -> logger.error("Error", tr.getCause())
        ).run();

        Mockito.verify(logger, Mockito.never()).error("Error");
    }

    @Test
    void streamWithException() {
        final Logger logger = Mockito.mock(Logger.class);
        final RuntimeException ex = new RuntimeException("Runtime");

        new FaultTolerant(
            Stream.of(
                () -> { throw ex; }
            ),
            tr -> logger.error("Error", tr.getCause())
        ).run();

        Mockito.verify(logger, Mockito.times(1)).error("Error", ex);
    }

    @Test
    void streamWithTwoExceptions() {
        final Logger logger = Mockito.mock(Logger.class);
        final RuntimeException ex = new RuntimeException("Runtime");

        new FaultTolerant(
            Stream.of(
                () -> { throw ex; },
                () -> {},
                () -> { throw ex; },
                () -> {}
            ),
            tr -> logger.error("Error", tr.getCause())
        ).run();

        Mockito.verify(logger, Mockito.times(2)).error("Error", ex);
    }
}
