package dev.iakunin.codexiabot.reddit.config;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Client {
    @Bean
    @Autowired
    public RedditClient redditClient(
        @Value("${app.reddit.username}") String username,
        @Value("${app.reddit.password}") String password,
        @Value("${app.reddit.client-id}") String clientId,
        @Value("${app.reddit.client-secret}") String clientSecret
    ) {
        UserAgent userAgent = new UserAgent(
            "bot",
            "dev.iakunin.codexiabot",
            "v0.1",
            username
        );

        Credentials credentials = Credentials.script(
            username,
            password,
            clientId,
            clientSecret
        );

        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

        return OAuthHelper.automatic(adapter, credentials);
    }
}
