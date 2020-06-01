package dev.iakunin.codexiabot.bot.toosmall;

import java.util.Map;
import org.cactoos.Text;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.NoNulls;
import org.cactoos.text.TextEnvelope;

final class MappedText extends TextEnvelope {

    MappedText(final Text source) {
        this(
            source,
            new MapOf<>(
                new MapEntry<>("Vue", "JavaScript")
            )
        );
    }

    private MappedText(final Text source, final Map<String, String> map) {
        super(
            new Unchecked<>(
                () -> map.getOrDefault(
                    new NoNulls(source).toString(),
                    new NoNulls(source).toString()
                )
            )
        );
    }
}
