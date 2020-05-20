package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaBadge;

public interface BadgeSender {
    void send(CodexiaBadge badge);
}
