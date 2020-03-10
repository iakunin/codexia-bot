package dev.iakunin.codexiabot.common.config.feign;

import dev.iakunin.codexiabot.common.config.service.SessionFingerprint;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public final class GeneralClientConfiguration {
    @Bean
    @Autowired
    public RequestInterceptor sessionFingerprintInterceptor(
        Properties properties,
        SessionFingerprint sessionFingerprint
    ) {
        return new SessionFingerprintInterceptor(
            properties.getSessionFingerprintHeaderName(),
            sessionFingerprint
        );
    }

    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }

    @Bean
    public Logger logger() {
        return new Slf4jFeignLogger();
    }
}
