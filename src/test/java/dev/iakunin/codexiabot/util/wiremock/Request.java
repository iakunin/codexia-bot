package dev.iakunin.codexiabot.util.wiremock;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import org.cactoos.Scalar;
import org.cactoos.scalar.NoNulls;

public final class Request implements Scalar<MappingBuilder> {

    private final Scalar<MappingBuilder> inner;

    public Request(String url) {
        this(WireMock.urlEqualTo(url));
    }

    public Request(UrlPattern url) {
        this(RequestMethod.GET, url);
    }

    public Request(
        RequestMethod method,
        String url
    ) {
        this(method.toString(), url);
    }

    public Request(
        String method,
        String url
    ) {
        this(method, WireMock.urlEqualTo(url));
    }

    public Request(
        String method,
        UrlPattern urlPattern
    ) {
        this(RequestMethod.fromString(method), urlPattern);
    }

    public Request(
        RequestMethod method,
        UrlPattern urlPattern
    ) {
        this(() -> WireMock.request(method.toString(), urlPattern));
    }

    public Request(Scalar<MappingBuilder> inner) {
        this.inner = new NoNulls<>(inner);
    }

    @Override
    public MappingBuilder value() throws Exception {
        return this.inner.value();
    }
}
