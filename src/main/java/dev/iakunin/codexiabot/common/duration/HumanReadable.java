package dev.iakunin.codexiabot.common.duration;

import dev.iakunin.codexiabot.common.text.Pluralized;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.iterable.IterableOf;
import org.cactoos.scalar.Mapped;
import org.cactoos.text.FormattedText;

public final class HumanReadable implements Text {

    private final Scalar<Text> scalar;

    public HumanReadable(Duration duration) {
        this(
            new Mapped<>(
                unit -> {
                    final long divided = duration.dividedBy(unit.getDuration());
                    return new FormattedText(
                        "%d %s",
                        divided,
                        new Pluralized(divided, unit).asString()
                    );
                },
                new BiggestUnit(
                    duration,
                    new IterableOf<>(
                        ChronoUnit.YEARS,
                        ChronoUnit.MONTHS,
                        ChronoUnit.WEEKS,
                        ChronoUnit.DAYS,
                        ChronoUnit.HOURS,
                        ChronoUnit.MINUTES,
                        ChronoUnit.SECONDS
                    )
                )
            )
        );
    }

    private HumanReadable(Scalar<Text> scalar) {
        this.scalar = scalar;
    }

    @Override
    public String asString() throws Exception {
        return this.scalar.value().asString();
    }
}
