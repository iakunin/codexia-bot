package dev.iakunin.codexiabot.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class SentryTest {

    @GetMapping(value = "/sentry/test")
    public ResponseEntity<String> testSentry(
    ) {
        this.throwRuntimeException();

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    private void throwRuntimeException() {
        throw new SentryException("This is a test exception for Sentry");
    }

    private static final class SentryException extends RuntimeException {
        public SentryException(String message) {
            super(message);
        }
    }
}
