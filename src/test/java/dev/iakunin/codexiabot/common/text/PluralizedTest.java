package dev.iakunin.codexiabot.common.text;

import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class PluralizedTest {

    @Test
    public void pluralizedString() throws Exception {
        assertEquals(
            "apples",
            new Pluralized(
                2,
                "apple"
            ).asString()
        );
    }

    @Test
    public void nonPluralizedString() throws Exception {
        assertEquals(
            "orange",
            new Pluralized(
                1,
                "orange"
            ).asString()
        );
    }

    @Test
    public void pluralizedText() throws Exception {
        assertEquals(
            "streams",
            new Pluralized(
                5,
                () -> "stream"
            ).asString()
        );
    }

    @Test
    public void nonPluralizedText() throws Exception {
        assertEquals(
            "test",
            new Pluralized(
                -1,
                () -> "test"
            ).asString()
        );
    }

    @Test
    public void pluralizedDays() throws Exception {
        assertEquals(
            "days",
            new Pluralized(
                10,
                ChronoUnit.DAYS
            ).asString()
        );
    }

    @Test
    public void nonPluralizedDays() throws Exception {
        assertEquals(
            "day",
            new Pluralized(
                1,
                ChronoUnit.DAYS
            ).asString()
        );
    }
}
