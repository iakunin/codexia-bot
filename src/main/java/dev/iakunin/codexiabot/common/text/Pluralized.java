package dev.iakunin.codexiabot.common.text;

import java.time.temporal.TemporalUnit;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.scalar.Ternary;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Lowered;
import org.cactoos.text.Sub;

final public class Pluralized implements Text {

    private final Scalar<Text> scalar;

    public Pluralized(long count, TemporalUnit unit) {
        this(
            count,
            new Lowered(
                new Sub(
                    unit.toString(),
                    0,
                    unit.toString().length() - 1
                )
            )
        );
    }

    public Pluralized(long count, String noun) {
        this(count, () -> noun);
    }

    public Pluralized(long count, Text noun) {
        this(
            () -> new FormattedText(
                new Ternary<>(
                    () -> Math.abs(count) > 1,
                    () -> "%ss",
                    () -> "%s"
                ).value(),
                noun.asString()
            )
        );
    }

    private Pluralized(Scalar<Text> scalar) {
        this.scalar = scalar;
    }

    @Override
    public String asString() throws Exception {
        return this.scalar.value().asString();
    }
}
