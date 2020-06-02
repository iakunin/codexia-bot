package dev.iakunin.codexiabot.util.wiremock;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import org.cactoos.Input;
import org.cactoos.Scalar;

public final class Stub implements Scalar<MappingBuilder> {

    private final Scalar<MappingBuilder> request;

    private final Scalar<ResponseDefinitionBuilder> response;

    public Stub(
        final String url,
        final Scalar<ResponseDefinitionBuilder> response
    ) {
        this(
            new Request(url),
            response
        );
    }

    public Stub(final String url, final String body) {
        this(
            new Request(url),
            new Response(body)
        );
    }

    public Stub(final String url, final Input body) {
        this(
            new Request(url),
            new Response(body)
        );
    }

    public Stub(
        final Scalar<MappingBuilder> request,
        final Scalar<ResponseDefinitionBuilder> response
    ) {
        this.request = request;
        this.response = response;
    }

    @Override
    public MappingBuilder value() throws Exception {
        return this.request
            .value()
            .willReturn(
                this.response.value()
            );
    }
}
