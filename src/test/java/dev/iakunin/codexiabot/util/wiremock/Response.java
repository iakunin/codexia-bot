package dev.iakunin.codexiabot.util.wiremock;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
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

    private static final int HTTP_OK = 200;

    private final Scalar<ResponseDefinitionBuilder> inner;

    public Response() {
        this("");
    }

    public Response(final int code) {
        this(code, "");
    }

    public Response(final String body) {
        this(HTTP_OK, body);
    }

    public Response(final Input body) {
        this(HTTP_OK, body);
    }

    public Response(final int code, final String body) {
        this(code, new TextOf(body));
    }

    public Response(final int code, final Input body) {
        this(code, new TextOf(body));
    }

    public Response(final int code, final Text body) {
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
        final int code,
        final Input body,
        final Map.Entry<String, String>... headers
    ) {
        this(code, new TextOf(body), headers);
    }

    @SafeVarargs
    public Response(
        final int code,
        final Text body,
        final Map.Entry<String, String>... headers
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
                    .withTransformers(ResponseTemplateTransformer.NAME)
        );
    }

    public Response(final Scalar<ResponseDefinitionBuilder> inner) {
        this.inner = new NoNulls<>(inner);
    }

    @Override
    public ResponseDefinitionBuilder value() throws Exception {
        return this.inner.value();
    }
}
