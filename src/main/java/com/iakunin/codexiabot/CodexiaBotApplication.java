package com.iakunin.codexiabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync(proxyTargetClass=true)
@EnableScheduling
@EnableFeignClients
public class CodexiaBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodexiaBotApplication.class, args);
    }
}
