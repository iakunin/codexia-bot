package dev.iakunin.codexiabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @checkstyle HideUtilityClassConstructor (500 lines)
 */
@SpringBootApplication(
    // https://stackoverflow.com/a/27389784/3456163
    exclude = {
        SecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class
    }
)
@EnableAsync
@EnableScheduling
@EnableFeignClients
@EnableTransactionManagement
public class CodexiaBotApplication {
    public static void main(final String[] args) {
        SpringApplication.run(CodexiaBotApplication.class, args);
    }
}
