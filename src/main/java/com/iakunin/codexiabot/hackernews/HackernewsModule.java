package com.iakunin.codexiabot.hackernews;

import java.util.stream.Stream;

public interface HackernewsModule {
    void healthCheckItems(Stream<Integer> externalIds);
}
