package dev.iakunin.codexiabot.common.config.service;

public interface SessionFingerprint {
    String get();

    void set(String fingerprint);

    void unset();
}
