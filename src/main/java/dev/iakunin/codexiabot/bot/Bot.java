package dev.iakunin.codexiabot.bot;

public interface Bot {
    enum Type {
        FOUND_ON_HACKERNEWS,
        FOUND_ON_REDDIT,
        STARS_UP,
        FORKS_UP,
        TOO_MANY_STARS,
    }
}
