package dev.iakunin.codexiabot.common.runnable;

import io.vavr.control.Try;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class FaultTolerant implements Runnable {

    private final Stream<Runnable> stream;

    private final Consumer<Try<?>> failure;

    @Override
    public void run() {
        this.stream
            .map(Try::runRunnable)
            .filter(Try::isFailure)
            .forEach(this.failure);
    }
}
