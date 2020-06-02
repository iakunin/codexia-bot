package dev.iakunin.codexiabot.common.duration;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import org.cactoos.iterable.IterableOf;
import org.cactoos.text.FormattedText;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BiggestUnitTest {

    @Test
    public void onlyMinutes() throws Exception {
        Assertions.assertEquals(
            ChronoUnit.MINUTES,
            new BiggestUnit(
                Duration.parse("PT1M")
            ).value()
        );
    }

    @Test
    public void minutesAndSeconds() throws Exception {
        Assertions.assertEquals(
            ChronoUnit.MINUTES,
            new BiggestUnit(
                Duration.parse("PT1M15S")
            ).value()
        );
    }

    @Test
    public void hoursAndMinutesAndSeconds() throws Exception {
        Assertions.assertEquals(
            ChronoUnit.HOURS,
            new BiggestUnit(
                Duration.parse("PT4H1M15S")
            ).value()
        );
    }

    @Test
    public void hoursWithPredefinedUnits() throws Exception {
        Assertions.assertEquals(
            ChronoUnit.HOURS,
            new BiggestUnit(
                Duration.parse("PT14H"),
                new IterableOf<>(
                    ChronoUnit.DAYS,
                    ChronoUnit.HOURS
                )
            ).value()
        );
    }

    @Test
    public void hoursWithPredefinedUnitsWrongOrder() throws Exception {
        Assertions.assertEquals(
            ChronoUnit.HOURS,
            new BiggestUnit(
                Duration.parse("PT15H"),
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

        final IOException exception = Assertions.assertThrows(
            IOException.class,
            () -> new BiggestUnit(duration, units).value()
        );
        Assertions.assertEquals(
            exception.getMessage(),
            new FormattedText(
                "Unable to find biggest unit; duration='%s'; units='%s'",
                duration,
                units
            ).asString()
        );
    }
}
