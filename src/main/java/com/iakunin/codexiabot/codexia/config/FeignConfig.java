package com.iakunin.codexiabot.codexia.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public final class FeignConfig {

    private final String codexiaToken;

    public FeignConfig(
        @Value("${app.codexia-token}")
        String codexiaToken
    ) {
        this.codexiaToken = codexiaToken;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Codexia-Token", this.codexiaToken);
        };
    }
}
