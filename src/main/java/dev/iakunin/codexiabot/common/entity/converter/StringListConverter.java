package dev.iakunin.codexiabot.common.entity.converter;

import java.util.List;
import java.util.Optional;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.cactoos.list.ListOf;

@Converter
public final class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(final List<String> list) {
        return Optional.ofNullable(list)
            .map(lst -> String.join(DELIMITER, list))
            .orElse("");
    }

    @Override
    public List<String> convertToEntityAttribute(final String joined) {
        return new ListOf<>(
            Optional.ofNullable(joined)
                .map(str -> str.split(DELIMITER))
                .orElse(new String[]{})
        );
    }
}
