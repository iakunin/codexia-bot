package dev.iakunin.codexiabot.hackernews.service;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("hackernews.service.GithubRepo")
@Slf4j
@RequiredArgsConstructor
public class GithubRepo implements Writer {

    private final GithubModule githubModule;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
