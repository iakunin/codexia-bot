package dev.iakunin.codexiabot.codexia.entity;

import org.cactoos.list.ListOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @checkstyle MagicNumber (500 lines)
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class CodexiaProjectUnitTest {

    @Test
    public void noBadges() {
        Assertions.assertEquals(
            0,
            new CodexiaProject().level()
        );
    }

    @Test
    public void newbieBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("newbie"));

        Assertions.assertEquals(
            0,
            project.level()
        );
    }

    @Test
    public void similarToLevel() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("olol12"));

        Assertions.assertEquals(
            0,
            project.level()
        );
    }

    @Test
    public void onlyZeroLevelBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L0"));

        Assertions.assertEquals(
            0,
            project.level()
        );
    }

    @Test
    public void bigLevelBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L123"));

        Assertions.assertEquals(
            123,
            project.level()
        );
    }

    @Test
    public void zeroLevelWithAnotherBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L0", "another"));

        Assertions.assertEquals(
            0,
            project.level()
        );
    }

    @Test
    public void firstLevelBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L1"));

        Assertions.assertEquals(
            1,
            project.level()
        );
    }

    @Test
    public void firstLevelWithAnotherBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L1", "another"));

        Assertions.assertEquals(
            1,
            project.level()
        );
    }

    @Test
    public void secondLevelBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L2"));

        Assertions.assertEquals(
            2,
            project.level()
        );
    }

    @Test
    public void secondLevelInLowerCaseBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("l2"));

        Assertions.assertEquals(
            2,
            project.level()
        );
    }

    @Test
    public void secondLevelWithAnotherBadge() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L2", "another"));

        Assertions.assertEquals(
            2,
            project.level()
        );
    }

    @Test
    public void multipleLevelsWithoutExternalId() {
        final var project = new CodexiaProject()
            .setBadges(new ListOf<>("L2", "L3"));

        final IllegalStateException exception = Assertions.assertThrows(
            IllegalStateException.class,
            project::level
        );
        Assertions.assertEquals(
            "Project cannot have more than one level; externalId='null'; levels='l2,l3'",
            exception.getMessage()
        );
    }

    @Test
    public void multipleLevelsWithExternalId() {
        final var project = new CodexiaProject()
            .setExternalId(321)
            .setBadges(new ListOf<>("L2", "L3"));

        final IllegalStateException exception = Assertions.assertThrows(
            IllegalStateException.class,
            project::level
        );
        Assertions.assertEquals(
            "Project cannot have more than one level; externalId='321'; levels='l2,l3'",
            exception.getMessage()
        );
    }
}
