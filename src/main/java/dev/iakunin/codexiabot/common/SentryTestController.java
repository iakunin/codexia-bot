package dev.iakunin.codexiabot.common;

import java.util.HashMap;
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

    @GetMapping(value = "/sentry/test2")
    ResponseEntity<HashMap> testSentry2(
    ) {
        final HashMap<Object, Object> hashMap = new HashMap<>() {{
            put("testKey", "testValue");
        }};

        this.throwRuntimeException();

        return new ResponseEntity<>(hashMap, HttpStatus.OK);
    }

    private void throwRuntimeException() {
        throw new RuntimeException("This is a test exception for Sentry");
    }
}
