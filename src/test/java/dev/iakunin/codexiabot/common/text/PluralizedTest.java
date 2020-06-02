package dev.iakunin.codexiabot.common.text;

import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @checkstyle MagicNumber (500 lines)
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class PluralizedTest {

    @Test
    public void pluralizedString() throws Exception {
        Assertions.assertEquals(
            "apples",
            new Pluralized(
                2,
                "apple"
            ).asString()
        );
    }

    @Test
    public void nonPluralizedString() throws Exception {
        Assertions.assertEquals(
            "orange",
            new Pluralized(
                1,
                "orange"
            ).asString()
        );
    }

    @Test
    public void pluralizedText() throws Exception {
        Assertions.assertEquals(
            "streams",
            new Pluralized(
                5,
                () -> "stream"
            ).asString()
        );
    }

    @Test
    public void nonPluralizedText() throws Exception {
        Assertions.assertEquals(
            "test",
            new Pluralized(
                -1,
                () -> "test"
            ).asString()
        );
    }

    @Test
    public void pluralizedDays() throws Exception {
        Assertions.assertEquals(
            "days",
            new Pluralized(
                10,
                ChronoUnit.DAYS
            ).asString()
        );
    }

    @Test
    public void nonPluralizedDays() throws Exception {
        Assertions.assertEquals(
            "day",
            new Pluralized(
                1,
                ChronoUnit.DAYS
            ).asString()
        );
    }
}
