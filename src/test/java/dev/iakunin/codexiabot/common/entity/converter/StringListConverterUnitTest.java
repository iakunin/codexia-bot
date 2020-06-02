package dev.iakunin.codexiabot.common.entity.converter;

import org.cactoos.list.ListOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class StringListConverterUnitTest {

    @Test
    public void toDatabaseColumnNull() {
        final var actual = new StringListConverter().convertToDatabaseColumn(null);

        Assertions.assertEquals("", actual);
    }

    @Test
    public void toDatabaseColumnOneItem() {
        final var actual = new StringListConverter().convertToDatabaseColumn(
            new ListOf<>("one")
        );

        Assertions.assertEquals("one", actual);
    }

    @Test
    public void toDatabaseColumnTwoItems() {
        final var actual = new StringListConverter().convertToDatabaseColumn(
            new ListOf<>("one", "two")
        );

        Assertions.assertEquals("one,two", actual);
    }

    @Test
    public void toEntityAttributeNull() {
        final var actual = new StringListConverter().convertToEntityAttribute(null);

        Assertions.assertEquals(new ListOf<String>(), actual);
    }

    @Test
    public void toEntityAttributeEmptyString() {
        final var actual = new StringListConverter().convertToEntityAttribute("");

        Assertions.assertEquals(new ListOf<>(""), actual);
    }

    @Test
    public void toEntityAttributeOneItem() {
        final var actual = new StringListConverter().convertToEntityAttribute("one");

        Assertions.assertEquals(new ListOf<>("one"), actual);
    }

    @Test
    public void toEntityAttributeTwoItems() {
        final var actual = new StringListConverter().convertToEntityAttribute("one,two");

        Assertions.assertEquals(new ListOf<>("one", "two"), actual);
    }

    @Test
    public void toEntityAttributeTwoItemsWithSpace() {
        final var actual = new StringListConverter().convertToEntityAttribute("one, two");

        Assertions.assertEquals(new ListOf<>("one", " two"), actual);
    }
}
