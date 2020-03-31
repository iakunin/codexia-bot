package dev.iakunin.codexiabot.util;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

public final class WireMockServer
extends com.github.tomakehurst.wiremock.WireMockServer
implements AutoCloseable {

    private static WireMockServer SERVER;

    private WireMockServer() {
        super(
            WireMockConfiguration.options()
                .dynamicPort()
                .extensions(new ResponseTemplateTransformer(false))
        );
    }

    public static WireMockServer getInstance() {
        if (SERVER == null) {
            SERVER = new WireMockServer();
            SERVER.start();
        }

        return SERVER;
    }

    @Override
    public void close() {
        SERVER.shutdown();
    }
}
