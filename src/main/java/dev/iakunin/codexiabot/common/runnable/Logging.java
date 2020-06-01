package dev.iakunin.codexiabot.common.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Logging implements Runnable {

    private final Runnable inner;

    private final Logger logger;

    public Logging(final Runnable inner) {
        this(
            inner,
            LoggerFactory.getLogger(Logging.class)
        );
    }

    public Logging(final Runnable inner, final Logger logger) {
        this.inner = inner;
        this.logger = logger;
    }

    @Override
    public void run() {
        this.logger.debug("Running {}", this.inner.getClass().getName());

        this.inner.run();

        this.logger.debug("Exiting from {}", this.inner.getClass().getName());
    }
}
