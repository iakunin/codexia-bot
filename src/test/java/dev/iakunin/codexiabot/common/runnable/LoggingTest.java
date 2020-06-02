package dev.iakunin.codexiabot.common.runnable;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;

public class LoggingTest {

    @Test
    public void run() {
        final Logger logger = Mockito.mock(Logger.class);

        new Logging(() -> { }, logger).run();

        final InOrder order = Mockito.inOrder(logger);
        order.verify(logger).debug(
            Mockito.eq("Running {}"),
            Mockito.any(String.class)
        );
        order.verify(logger).debug(
            Mockito.eq("Exiting from {}"),
            Mockito.any(String.class)
        );
    }
}
