package dev.iakunin.codexiabot.common.duration;

import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class HumanReadableTest {

    @Test
    public void onlyMinutes() throws Exception {
        assertEquals(
            "15 minutes",
            new HumanReadable(
                Duration.parse("PT15M")
            ).asString()
        );
    }

    @Test
    public void minutesAndSeconds() throws Exception {
        assertEquals(
            "15 minutes",
            new HumanReadable(
                Duration.parse("PT15M47S")
            ).asString()
        );
    }

    @Test
    public void hoursMinutesAndSeconds() throws Exception {
        assertEquals(
            "4 hours",
            new HumanReadable(
                Duration.parse("PT4H15M47S")
            ).asString()
        );
    }

    @Test
    public void hourMinutesAndSeconds() throws Exception {
        assertEquals(
            "1 hour",
            new HumanReadable(
                Duration.parse("PT1H15M47S")
            ).asString()
        );
    }

    @Test
    public void dayHourMinutesAndSeconds() throws Exception {
        assertEquals(
            "1 day",
            new HumanReadable(
                Duration.ofDays(1).plus(
                    Duration.ofHours(4)
                )
            ).asString()
        );
    }

    @Test
    public void week() throws Exception {
        assertEquals(
            "1 week",
            new HumanReadable(
                Duration.ofDays(9).plus(
                    Duration.ofHours(4)
                )
            ).asString()
        );
    }

    @Test
    public void twoWeek() throws Exception {
        assertEquals(
            "2 weeks",
            new HumanReadable(
                Duration.ofDays(16).plus(
                    Duration.ofHours(4)
                )
            ).asString()
        );
    }
}
