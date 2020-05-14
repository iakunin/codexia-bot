package dev.iakunin.codexiabot.util;

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
        NetworkAdapter original,
        HttpUrl url
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
    public void setUserAgent(@NotNull UserAgent userAgent) {
        this.original.setUserAgent(userAgent);
    }

    @NotNull
    @Override
    public WebSocket connect(@NotNull String s, @NotNull WebSocketListener webSocketListener) {
        return this.original.connect(s, webSocketListener);
    }

    @NotNull
    @Override
    public HttpResponse execute(@NotNull HttpRequest r) {
        final HttpUrl parsedUrl = HttpUrl.parse(r.getUrl());

        if (parsedUrl == null) {
            return this.original.execute(r);
        }

        return this.original
            .execute(
                r.newBuilder()
                    .url(
                        parsedUrl.newBuilder()
                            .scheme(this.url.scheme())
                            .host(this.url.host())
                            .port(this.url.port())
                            .encodedPath(
                                this.url.encodedPath() + parsedUrl.encodedPath()
                            )
                            .build()
                    ).build()
            );
    }
}
