package dev.iakunin.codexiabot.bot.toosmall;

import org.cactoos.Scalar;
import org.cactoos.iterable.IterableOf;
import org.cactoos.scalar.FallbackFrom;
import org.cactoos.scalar.ScalarWithFallback;

// @todo #136 replace this class with just ScalarWithFallback when
//  `cactoos` new version is released
public final class FallbackFromNull<T> implements Scalar<T> {

    private final Scalar<T> inner;

    private final Scalar<T> fallback;

    public FallbackFromNull(Scalar<T> inner, Scalar<T> fallback) {
        this.inner = inner;
        this.fallback = fallback;
    }

    @Override
    public T value() throws Exception {
        return
            new ScalarWithFallback<>(
                this.inner,
                new IterableOf<>(
                    new FallbackFrom<>(
                        RuntimeException.class,
                        exception -> this.fallback.value()
                    )
                )
            ).value();
    }
}
