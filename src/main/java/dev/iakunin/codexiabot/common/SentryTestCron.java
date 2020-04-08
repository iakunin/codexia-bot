package dev.iakunin.codexiabot.common;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SentryTestCron {

    @Scheduled(cron="0 * * * * *")
    public void run() {
        this.unsafeMethod();
    }

    /**
     * An example method that throws an exception.
     */
    void unsafeMethod() {
        throw new UnsupportedOperationException("You shouldn't call this!");
    }
}
