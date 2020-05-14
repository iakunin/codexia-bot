package dev.iakunin.codexiabot.config;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import dev.iakunin.codexiabot.CodexiaBotApplication;
import dev.iakunin.codexiabot.util.WireMockServer;
import dev.iakunin.codexiabot.util.WiremockNetworkingAdapter;
import dev.iakunin.codexiabot.util.wiremock.Request;
import dev.iakunin.codexiabot.util.wiremock.Response;
import dev.iakunin.codexiabot.util.wiremock.Stub;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import okhttp3.HttpUrl;
import org.cactoos.io.ResourceOf;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import(CodexiaBotApplication.class)
public class RedditConfig {

    @Bean
    public RedditClient redditClient() {
        WireMockServer.stub(
            new Stub(
                "/reddit/api/v1/me",
                new Response(
                    new ResourceOf("wiremock/config/reddit/me.json")
                )
            )
        );
        WireMockServer.stub(
            new Stub(
                new Request(RequestMethod.POST, "/reddit/api/v1/access_token"),
                new Response(
                    new ResourceOf("wiremock/config/reddit/accessToken.json")
                )
            )
        );

        final RedditClient client = OAuthHelper.automatic(
            new WiremockNetworkingAdapter(
                new OkHttpNetworkAdapter(
                    new UserAgent("userAgent")
                ),
                HttpUrl.parse(
                    WireMockServer.getInstance().baseUrl() + "/reddit"
                )
            ),
            Credentials.script(
                "username",
                "password",
                "clientId",
                "clientSecret"
            )
        );
        client.setLogHttp(false);

        return client;
    }
}
