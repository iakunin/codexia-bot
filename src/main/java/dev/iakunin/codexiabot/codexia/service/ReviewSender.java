package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;

public interface ReviewSender {
    void send(CodexiaReview review);
}
