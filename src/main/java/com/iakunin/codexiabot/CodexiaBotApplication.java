package com.iakunin.codexiabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
    // https://stackoverflow.com/a/27389784/3456163
    exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
    }
)
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
@EnableFeignClients
public class CodexiaBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodexiaBotApplication.class, args);
    }
}
