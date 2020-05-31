package dev.iakunin.codexiabot.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;

public final class WireMockWrapper implements AutoCloseable {

    private static final Unchecked<WireMockServer> SERVER = new Unchecked<>(
        new Sticky<>(
            () -> {
                final var server = new WireMockServer(
                    WireMockConfiguration.options()
                        .dynamicPort()
                        .extensions(new ResponseTemplateTransformer(false))
                );
                server.start();
                return server;
            }
        )
    );

    public void stub(final Stub stub) {
        SERVER.value()
            .stubFor(
                new Unchecked<>(stub).value()
            );
    }

    public String baseUrl() {
        return SERVER.value().baseUrl();
    }

    public void resetAll() {
        SERVER.value().resetAll();
    }

    public void verify(final int count, final RequestPatternBuilder pattern) {
        SERVER.value().verify(count, pattern);
    }

    @Override
    public void close() {
        SERVER.value().shutdown();
    }
}
