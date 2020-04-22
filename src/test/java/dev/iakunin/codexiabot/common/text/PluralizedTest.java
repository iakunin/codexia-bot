package dev.iakunin.codexiabot.common.text;

import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class PluralizedTest {

    @Test
    void pluralizedString() throws Exception {
        assertEquals(
            "apples",
            new Pluralized(
                2,
                "apple"
            ).asString()
        );
    }

    @Test
    void nonPluralizedString() throws Exception {
        assertEquals(
            "orange",
            new Pluralized(
                1,
                "orange"
            ).asString()
        );
    }

    @Test
    void pluralizedText() throws Exception {
        assertEquals(
            "streams",
            new Pluralized(
                5,
                () -> "stream"
            ).asString()
        );
    }

    @Test
    void nonPluralizedText() throws Exception {
        assertEquals(
            "test",
            new Pluralized(
                -1,
                () -> "test"
            ).asString()
        );
    }

    @Test
    void pluralizedDays() throws Exception {
        assertEquals(
            "days",
            new Pluralized(
                10,
                ChronoUnit.DAYS
            ).asString()
        );
    }

    @Test
    void nonPluralizedDays() throws Exception {
        assertEquals(
            "day",
            new Pluralized(
                1,
                ChronoUnit.DAYS
            ).asString()
        );
    }
}
