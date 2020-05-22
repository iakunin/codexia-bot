package dev.iakunin.codexiabot.common.entity.converter;

import java.util.List;
import java.util.Optional;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.cactoos.list.ListOf;

@Converter
public final class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        if (list == null) return "";

        return String.join(",", list);
    }

    @Override
    public List<String> convertToEntityAttribute(String joined) {
        return new ListOf<>(
            Optional.ofNullable(joined)
                .map(str -> str.split(","))
                .orElse(new String[]{})
        );
    }
}
