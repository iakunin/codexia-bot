package dev.iakunin.codexiabot.common.runnable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public final class Logging implements Runnable {

    private final Runnable inner;

    @Override
    public void run() {
        log.debug("Running {}", this.inner.getClass().getName());

        this.inner.run();

        log.debug("Exiting from {}", this.inner.getClass().getName());
    }
}
