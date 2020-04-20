package dev.iakunin.codexiabot.common.runnable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public final class Logging implements Runnable {

    private final Runnable inner;

    @Override
    public void run() {
        log.info("Running {}", this.inner.getClass().getName());

        this.inner.run();

        log.info("Exiting from {}", this.inner.getClass().getName());
    }
}
