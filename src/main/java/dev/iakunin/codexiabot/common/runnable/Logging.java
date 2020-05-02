package dev.iakunin.codexiabot.common.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Logging implements Runnable {

    private final Runnable inner;

    private final Logger logger;

    public Logging(Runnable inner) {
        this(
            inner,
            LoggerFactory.getLogger(Logging.class)
        );
    }

    public Logging(Runnable inner, Logger logger) {
        this.inner = inner;
        this.logger = logger;
    }

    @Override
    public void run() {
        logger.info("Running {}", this.inner.getClass().getName());

        this.inner.run();

        logger.info("Exiting from {}", this.inner.getClass().getName());
    }
}
