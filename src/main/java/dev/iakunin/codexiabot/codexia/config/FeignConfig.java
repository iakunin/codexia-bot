package dev.iakunin.codexiabot.codexia.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public final class FeignConfig {

    private final String token;

    public FeignConfig(
        @Value("${app.codexia.token}")
        final String token
    ) {
        this.token = token;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header("X-Codexia-Token", this.token);
    }
}
