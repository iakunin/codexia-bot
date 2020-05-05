package dev.iakunin.codexiabot.util.wiremock;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import java.util.Map;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.iterable.Mapped;
import org.cactoos.map.MapEntry;
import org.cactoos.scalar.NoNulls;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

public final class Response implements Scalar<ResponseDefinitionBuilder> {

    private final Scalar<ResponseDefinitionBuilder> inner;

    public Response(String body) {
        this(200, body);
    }

    public Response(Input body) {
        this(200, body);
    }

    public Response(int code, String body) {
        this(code, new TextOf(body));
    }

    public Response(int code, Input body) {
        this(code, new TextOf(body));
    }

    public Response(int code, Text body) {
        this(
            code,
            body,
            new MapEntry<>(
                com.google.common.net.HttpHeaders.CONTENT_TYPE,
                "application/json"
            )
        );
    }

    @SafeVarargs
    public Response(
        int code,
        Input body,
        Map.Entry<String, String>... headers
    ) {
        this(
            code,
            new TextOf(body),
            headers
        );
    }

    @SafeVarargs
    public Response(
        int code,
        Text body,
        Map.Entry<String, String>... headers
    ) {
        this(
            () ->
                WireMock.aResponse()
                    .withStatus(code)
                    .withHeaders(
                        new HttpHeaders(
                            new Mapped<>(
                                entry -> new HttpHeader(
                                    entry.getKey(),
                                    entry.getValue()
                                ),
                                headers
                            )
                        )
                    )
                    .withBody(
                        new UncheckedText(body).asString()
                    )
        );
    }

    public Response(Scalar<ResponseDefinitionBuilder> inner) {
        this.inner = new NoNulls<>(inner);
    }

    @Override
    public ResponseDefinitionBuilder value() throws Exception {
        return this.inner.value();
    }
}
