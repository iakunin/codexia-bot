package dev.iakunin.codexiabot.reddit.config;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @checkstyle DesignForExtension (500 lines)
 * @checkstyle ParameterNumber (500 lines)
 */
@SuppressWarnings("PMD.UseObjectForClearerAPI")
@Configuration
public class Client {

    @Bean
    @Autowired
    public RedditClient redditClient(
        @Value("${app.reddit.username}") final String username,
        @Value("${app.reddit.password}") final String password,
        @Value("${app.reddit.client-id}") final String id,
        @Value("${app.reddit.client-secret}") final String secret
    ) {
        final RedditClient client = OAuthHelper.automatic(
            new OkHttpNetworkAdapter(
                new UserAgent(
                    "bot",
                    "dev.iakunin.codexiabot",
                    "v0.1",
                    username
                )
            ),
            Credentials.script(username, password, id, secret)
        );
        client.setLogHttp(false);

        return client;
    }
}
