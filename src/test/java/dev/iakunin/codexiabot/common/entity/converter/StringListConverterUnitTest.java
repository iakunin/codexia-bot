package dev.iakunin.codexiabot.common.entity.converter;

import org.cactoos.list.ListOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class StringListConverterUnitTest {

    @Test
    public void convertToDatabaseColumnNull() {
        final var actual = new StringListConverter().convertToDatabaseColumn(null);

        Assertions.assertEquals("", actual);
    }

    @Test
    public void convertToDatabaseColumnOneItem() {
        final var actual = new StringListConverter().convertToDatabaseColumn(
            new ListOf<>("one")
        );

        Assertions.assertEquals("one", actual);
    }

    @Test
    public void convertToDatabaseColumnTwoItems() {
        final var actual = new StringListConverter().convertToDatabaseColumn(
            new ListOf<>("one", "two")
        );

        Assertions.assertEquals("one,two", actual);
    }

    @Test
    public void convertToEntityAttributeNull() {
        final var actual = new StringListConverter().convertToEntityAttribute(null);

        Assertions.assertEquals(new ListOf<String>(), actual);
    }

    @Test
    public void convertToEntityAttributeEmptyString() {
        final var actual = new StringListConverter().convertToEntityAttribute("");

        Assertions.assertEquals(new ListOf<>(""), actual);
    }

    @Test
    public void convertToEntityAttributeOneItem() {
        final var actual = new StringListConverter().convertToEntityAttribute("one");

        Assertions.assertEquals(new ListOf<>("one"), actual);
    }

    @Test
    public void convertToEntityAttributeTwoItems() {
        final var actual = new StringListConverter().convertToEntityAttribute("one,two");

        Assertions.assertEquals(new ListOf<>("one", "two"), actual);
    }

    @Test
    public void convertToEntityAttributeTwoItemsWithSpace() {
        final var actual = new StringListConverter().convertToEntityAttribute("one, two");

        Assertions.assertEquals(new ListOf<>("one", " two"), actual);
    }
}
