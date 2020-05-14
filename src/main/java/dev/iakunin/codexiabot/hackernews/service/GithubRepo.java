package dev.iakunin.codexiabot.hackernews.service;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("hackernews.service.GithubRepo")
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class GithubRepo implements Writer {

    private final GithubModule githubModule;

    @Override
    public void write(HackernewsItem item) {
        if (
            item.getUrl() != null &&
            item.getUrl().contains("github.com") &&
            !item.getUrl().contains("gist.github.com")
        ) {
            try {
                this.githubModule.createRepo(
                    new GithubModule.CreateArguments(
                        item.getUrl(),
                        GithubModule.Source.HACKERNEWS,
                        String.valueOf(item.getExternalId())
                    )
                );
            } catch (RuntimeException | IOException e) {
                log.error("Unable to create github repo; source url='{}'", item.getUrl(), e);
            }
        }
    }
}
