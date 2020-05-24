package dev.iakunin.codexiabot.common.duration;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import org.cactoos.iterable.IterableOf;
import org.cactoos.text.FormattedText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class BiggestUnitTest {

    @Test
    public void onlyMinutes() throws Exception {
        assertEquals(
            ChronoUnit.MINUTES,
            new BiggestUnit(
                Duration.parse("PT1M")
            ).value()
        );
    }

    @Test
    public void minutesAndSeconds() throws Exception {
        assertEquals(
            ChronoUnit.MINUTES,
            new BiggestUnit(
                Duration.parse("PT1M15S")
            ).value()
        );
    }

    @Test
    public void hoursAndMinutesAndSeconds() throws Exception {
        assertEquals(
            ChronoUnit.HOURS,
            new BiggestUnit(
                Duration.parse("PT4H1M15S")
            ).value()
        );
    }

    @Test
    public void hoursWithPredefinedUnits() throws Exception {
        assertEquals(
            ChronoUnit.HOURS,
            new BiggestUnit(
                Duration.parse("PT16H"),
                new IterableOf<>(
                    ChronoUnit.DAYS,
                    ChronoUnit.HOURS
                )
            ).value()
        );
    }

    @Test
    public void hoursWithPredefinedUnitsWrongOrder() throws Exception {
        assertEquals(
            ChronoUnit.HOURS,
            new BiggestUnit(
                Duration.parse("PT16H"),
                new IterableOf<>(
                    ChronoUnit.MINUTES,
                    ChronoUnit.HOURS,
                    ChronoUnit.DAYS
                )
            ).value()
        );
    }

    @Test
    public void lessThanMinUnit() throws IOException {
        final Duration duration = Duration.parse("PT16H");
        final IterableOf<TemporalUnit> units = new IterableOf<>(ChronoUnit.DAYS);

        final IOException exception = assertThrows(
            IOException.class,
            () -> new BiggestUnit(duration, units).value()
        );
        assertEquals(
            exception.getMessage(),
            new FormattedText(
                "Unable to find biggest unit; duration='%s'; units='%s'",
                duration,
                units
            ).asString()
        );
    }
}
