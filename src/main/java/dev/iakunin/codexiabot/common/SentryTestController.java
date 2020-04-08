package dev.iakunin.codexiabot.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SentryTestController {

    @RequestMapping(
        value = "/sentry/test",
        method = RequestMethod.GET
    )
    public ResponseEntity<String> sentryTest() {
        this.unsafeMethod();

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    /**
     * An example method that throws an exception.
     */
    void unsafeMethod() {
        throw new UnsupportedOperationException("You shouldn't call this!");
    }
}
