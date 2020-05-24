package dev.iakunin.codexiabot.common.entity.converter;

import org.cactoos.list.ListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class StringListConverterUnitTest {

    @Test
    public void toDatabaseColumnNull() {
        final var actual = new StringListConverter().convertToDatabaseColumn(null);

        assertEquals("", actual);
    }

    @Test
    public void toDatabaseColumnOneItem() {
        final var actual = new StringListConverter().convertToDatabaseColumn(
            new ListOf<>("one")
        );

        assertEquals("one", actual);
    }

    @Test
    public void toDatabaseColumnTwoItems() {
        final var actual = new StringListConverter().convertToDatabaseColumn(
            new ListOf<>("one", "two")
        );

        assertEquals("one,two", actual);
    }

    @Test
    public void toEntityAttributeNull() {
        final var actual = new StringListConverter().convertToEntityAttribute(null);

        assertEquals(new ListOf<String>(), actual);
    }

    @Test
    public void toEntityAttributeEmptyString() {
        final var actual = new StringListConverter().convertToEntityAttribute("");

        assertEquals(new ListOf<>(""), actual);
    }

    @Test
    public void toEntityAttributeOneItem() {
        final var actual = new StringListConverter().convertToEntityAttribute("one");

        assertEquals(new ListOf<>("one"), actual);
    }

    @Test
    public void toEntityAttributeTwoItems() {
        final var actual = new StringListConverter().convertToEntityAttribute("one,two");

        assertEquals(new ListOf<>("one", "two"), actual);
    }

    @Test
    public void toEntityAttributeTwoItemsWithSpace() {
        final var actual = new StringListConverter().convertToEntityAttribute("one, two");

        assertEquals(new ListOf<>("one", " two"), actual);
    }
}
