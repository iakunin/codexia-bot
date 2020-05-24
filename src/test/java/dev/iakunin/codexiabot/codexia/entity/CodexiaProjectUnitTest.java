package dev.iakunin.codexiabot.codexia.entity;

import org.cactoos.list.ListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class CodexiaProjectUnitTest {

    @Test
    public void noBadges() {
        assertEquals(
            0,
            new CodexiaProject().level()
        );
    }

    @Test
    public void newbieBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("newbie"));

        assertEquals(
            0,
            project.level()
        );
    }

    @Test
    public void similarToLevel() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("olol12"));

        assertEquals(
            0,
            project.level()
        );
    }

    @Test
    public void onlyL0Badge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L0"));

        assertEquals(
            0,
            project.level()
        );
    }

    @Test
    public void onlyL123Badge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L123"));

        assertEquals(
            123,
            project.level()
        );
    }

    @Test
    public void l0WithAnotherBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L0", "another"));

        assertEquals(
            0,
            project.level()
        );
    }

    @Test
    public void l1Badge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L1"));

        assertEquals(
            1,
            project.level()
        );
    }

    @Test
    public void l1WithAnotherBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L1", "another"));

        assertEquals(
            1,
            project.level()
        );
    }

    @Test
    public void l2Badge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L2"));

        assertEquals(
            2,
            project.level()
        );
    }


    @Test
    public void l2InLowerCaseBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("l2"));

        assertEquals(
            2,
            project.level()
        );
    }

    @Test
    public void l2WithAnotherBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L2", "another"));

        assertEquals(
            2,
            project.level()
        );
    }

    @Test
    public void multipleLevelsWithoutExternalId() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L2", "L3"));

        final IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            project::level
        );
        assertEquals(
            "Project cannot have more than one level; externalId='null'; levels='l2,l3'",
            exception.getMessage()
        );
    }

    @Test
    public void multipleLevelsWithExternalId() {
        final var project = new CodexiaProject()
            .setExternalId(321)
            .setBadges(new ListOf<>("L2", "L3"));

        final IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            project::level
        );
        assertEquals(
            "Project cannot have more than one level; externalId='321'; levels='l2,l3'",
            exception.getMessage()
        );
    }
}
