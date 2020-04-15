package dev.iakunin.codexiabot.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class SentryTestController {

    @GetMapping(value = "/sentry/test")
    ResponseEntity<String> testSentry(
    ) {
        this.throwRuntimeException();

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    private void throwRuntimeException() {
        throw new RuntimeException("This is a test exception for Sentry");
    }
}
