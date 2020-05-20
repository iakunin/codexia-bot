package dev.iakunin.codexiabot.bot.found;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.hackernews.HackernewsModule;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class Hackernews implements Bot {

    private final GithubModule githubModule;

    private final CodexiaModule codexiaModule;

    private final HackernewsModule hackernewsModule;

    @Override
    public Stream<GithubRepo> repoStream() {
        return this.githubModule
            .findAllInCodexiaAndHackernews();
    }

    @Override
    public GithubModule.Source source() {
        return GithubModule.Source.HACKERNEWS;
    }

    @Override
    public String reviewText(String externalId) {
        return
            new FormattedText(
                new Joined(
                    " ",
                    "This project is found on Hackernews (%d upvotes):",
                    "[web link](https://news.ycombinator.com/item?id=%s),",
                    "[api link](https://hacker-news.firebaseio.com/v0/item/%s.json)."
                ),
                this.upvotes(externalId),
                externalId,
                externalId
            ).toString();
    }

    @Override
    public CodexiaMeta meta(CodexiaReview review) {
        try (var reviews = this.codexiaModule
            .findAllReviews(review.getCodexiaProject(), review.getAuthor())
        ) {
            return new CodexiaMeta()
                .setCodexiaProject(review.getCodexiaProject())
                .setKey("hacker-news-id")
                .setValue(
                    reviews
                        .map(CodexiaReview::getReason)
                        .collect(Collectors.joining(","))
                );
        }
    }

    private int upvotes(String externalId) {
        return this.hackernewsModule
            .getItem(Integer.valueOf(externalId))
            .getScore();
    }
}
