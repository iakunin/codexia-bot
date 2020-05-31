package dev.iakunin.codexiabot.util;

import java.util.Optional;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.http.HttpResponse;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.UserAgent;
import okhttp3.HttpUrl;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;

public final class WiremockNetworkingAdapter implements NetworkAdapter {

    private final NetworkAdapter original;

    private final HttpUrl url;

    public WiremockNetworkingAdapter(
        final NetworkAdapter original,
        final HttpUrl url
    ) {
        this.original = original;
        this.url = url;
    }

    @NotNull
    @Override
    public UserAgent getUserAgent() {
        return this.original.getUserAgent();
    }

    @Override
    public void setUserAgent(@NotNull final UserAgent useragent) {
        this.original.setUserAgent(useragent);
    }

    @NotNull
    @Override
    public WebSocket connect(
        @NotNull final String uri,
        @NotNull final WebSocketListener listener
    ) {
        return this.original.connect(uri, listener);
    }

    @NotNull
    @Override
    public HttpResponse execute(@NotNull final HttpRequest request) {
        return Optional
            .ofNullable(HttpUrl.parse(request.getUrl()))
            .map(parsed -> this.original
                .execute(
                    request.newBuilder().url(
                        parsed.newBuilder()
                            .scheme(this.url.scheme())
                            .host(this.url.host())
                            .port(this.url.port())
                            .encodedPath(
                                this.url.encodedPath() + parsed.encodedPath()
                            ).build()
                    ).build()
                )
            ).orElseGet(() -> this.original.execute(request));
    }
}
