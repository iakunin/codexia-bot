package dev.iakunin.codexiabot.common.duration;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;
import org.cactoos.Scalar;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterable.Sorted;
import org.cactoos.scalar.ItemAt;
import org.cactoos.text.FormattedText;

final class BiggestUnit implements Scalar<TemporalUnit> {

    private final Scalar<TemporalUnit> scalar;

    BiggestUnit(Duration duration) {
        this(duration, ChronoUnit.values());
    }

    BiggestUnit(Duration duration, TemporalUnit... units) {
        this(duration, new IterableOf<>(units));
    }

    BiggestUnit(Duration duration, Iterable<TemporalUnit> units) {
        this(
            new ItemAt<TemporalUnit>(
                0,
                itr -> {
                    throw new IOException(
                        new FormattedText(
                            "Unable to find biggest unit; duration='%s'; units='%s'",
                            duration,
                            units
                        ).asString()
                    );
                },
                new Filtered<>(
                    unit -> duration.dividedBy(unit.getDuration()) != 0,
                    new Sorted<>(
                        Comparator.comparing(
                            TemporalUnit::getDuration,
                            Comparator.reverseOrder()
                        ),
                        units
                    )
                )
            )
        );
    }

    private BiggestUnit(Scalar<TemporalUnit> scalar) {
        this.scalar = scalar;
    }

    @Override
    public TemporalUnit value() throws Exception {
        return this.scalar.value();
    }
}
