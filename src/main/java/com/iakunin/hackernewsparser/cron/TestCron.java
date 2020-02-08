package com.iakunin.hackernewsparser.cron;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public final class TestCron {

    @Scheduled(cron="0 * * * * *")  // once per minute
    public void calculateTourSchedules() {
        log.info("I am a test cron! Hello!");
    }
}
