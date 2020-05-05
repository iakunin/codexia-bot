package dev.iakunin.codexiabot.util.wiremock;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.Stubbing;
import dev.iakunin.codexiabot.util.WireMockServer;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.scalar.Unchecked;

public final class Stub implements Runnable {

    private final Stubbing wiremock;

    private final Scalar<MappingBuilder> request;

    private final Scalar<ResponseDefinitionBuilder> response;

    public Stub(
        String url,
        Scalar<ResponseDefinitionBuilder> response
    ) {
        this(
            new Request(url),
            response
        );
    }

    public Stub(String url, String body) {
        this(
            new Request(url),
            new Response(body)
        );
    }

    public Stub(String url, Input body) {
        this(
            new Request(url),
            new Response(body)
        );
    }

    public Stub(
        Scalar<MappingBuilder> request,
        Scalar<ResponseDefinitionBuilder> response
    ) {
        this(
            WireMockServer.getInstance(),
            request,
            response
        );
    }

    public Stub(
        Stubbing wiremock,
        Scalar<MappingBuilder> request,
        Scalar<ResponseDefinitionBuilder> response
    ) {
        this.wiremock = wiremock;
        this.request = request;
        this.response = response;
    }

    @Override
    public void run() {
        this.wiremock.stubFor(
            new Unchecked<>(this.request)
                .value()
                .willReturn(
                    new Unchecked<>(
                        this.response
                    ).value()
                )
        );
    }
}
