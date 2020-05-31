package dev.iakunin.codexiabot.util.wiremock;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import org.cactoos.Scalar;
import org.cactoos.scalar.NoNulls;

public final class Request implements Scalar<MappingBuilder> {

    private final Scalar<MappingBuilder> inner;

    public Request(final String url) {
        this(WireMock.urlEqualTo(url));
    }

    public Request(final UrlPattern url) {
        this(RequestMethod.GET, url);
    }

    public Request(
        final RequestMethod method,
        final String url
    ) {
        this(method.toString(), url);
    }

    public Request(
        final String method,
        final String url
    ) {
        this(method, WireMock.urlEqualTo(url));
    }

    public Request(
        final String method,
        final UrlPattern pattern
    ) {
        this(RequestMethod.fromString(method), pattern);
    }

    public Request(
        final RequestMethod method,
        final UrlPattern pattern
    ) {
        this(() -> WireMock.request(method.toString(), pattern));
    }

    public Request(final Scalar<MappingBuilder> inner) {
        this.inner = new NoNulls<>(inner);
    }

    @Override
    public MappingBuilder value() throws Exception {
        return this.inner.value();
    }
}
